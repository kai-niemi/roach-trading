package io.roach.trading.aspect;

import java.lang.annotation.Annotation;
import java.sql.SQLException;
import java.time.Duration;
import java.time.Instant;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.dao.ConcurrencyFailureException;
import org.springframework.dao.DataAccessException;
import org.springframework.transaction.TransactionSystemException;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.springframework.util.Assert;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.roach.trading.annotation.AdvisorOrder;
import io.roach.trading.annotation.Retryable;
import jakarta.annotation.PostConstruct;

/**
 * AOP aspect that applies retrys of failed transactions in the main business services.
 * This aspect intercepts and handles concurrency failures such as deadlock looser,
 * pessimistic and optimistic locking failures.
 * <p/>
 * Its most useful with an optimistic locking strategy.
 *
 * @see Retryable
 */
@Aspect
@Order(AdvisorOrder.TX_RETRY_ADVISOR)
public class TransactionRetryAspect {
    public static <A extends Annotation> A findAnnotation(ProceedingJoinPoint pjp, Class<A> annotationType) {
        return AnnotationUtils
                .findAnnotation(pjp.getSignature().getDeclaringType(), annotationType);
    }

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private MeterRegistry meterRegistry;

    private Counter successCounter;

    private Counter abortCounter;

    private Counter retryCounter;

    @PostConstruct
    public void init() {
        successCounter = meterRegistry.counter("trading.txn.success");
        abortCounter = meterRegistry.counter("trading.txn.abort");
        retryCounter = meterRegistry.counter("trading.txn.retry");
    }

    @Around(value = "Pointcuts.retryableBoundaryOperation(retryable)",
            argNames = "pjp,retryable")
    public Object doAroundRetryable(ProceedingJoinPoint pjp, Retryable retryable)
            throws Throwable {
        Assert.isTrue(!TransactionSynchronizationManager.isActualTransactionActive(), "tx active");

        // Grab from type if needed (for non-annotated methods)
        if (retryable == null) {
            retryable = findAnnotation(pjp, Retryable.class);
        }

        int numCalls = 0;

        final Instant callTime = Instant.now();

        do {
            try {
                numCalls++;
                Object rv = pjp.proceed();
                successCounter.increment();
                if (numCalls > 1) {
                    logger.debug(
                            "Transient error recovered after " + numCalls + " of " + retryable
                                    .retryAttempts() + " retries ("
                                    + Duration.between(callTime, Instant.now()).toString() + ")");
                }
                return rv;
            } catch (DataAccessException | TransactionSystemException ex) { // TX abort on commit's
                Throwable cause = NestedExceptionUtils.getMostSpecificCause(ex);
                if (cause instanceof SQLException) {
                    SQLException sqlException = (SQLException) cause;
                    meterRegistry.counter("trade.txt.error." + sqlException.getSQLState()).increment();
                    if ("40001".equals(sqlException.getSQLState())) { // Transient error code
                        handleTransientException(sqlException, numCalls, pjp.getSignature().toShortString(),
                                retryable.maxBackoff());
                        continue;
                    }
                }

                logger.error("Non-recoverable exception in retry loop", ex);

                abortCounter.increment();
                throw ex;
            }
        } while (numCalls < retryable.retryAttempts());

        throw new ConcurrencyFailureException("Too many transient errors (" + numCalls + ") for method ["
                + pjp.getSignature().toShortString() + "]. Giving up!");
    }

    private void handleTransientException(SQLException ex, int numCalls, String method, long maxBackoff) {
        retryCounter.increment();

        try {
            long backoffMillis = Math.min((long) (Math.pow(2, numCalls) + Math.random() * 1000), maxBackoff);
            if (logger.isWarnEnabled()) {
                logger.warn("Transient error detected (backoff {}ms) in call {} to '{}': {}",
                        backoffMillis, numCalls, method, ex.getMessage());
            }
            Thread.sleep(backoffMillis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
