package io.roach.trading.aspect;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import io.roach.trading.annotation.Retryable;
import io.roach.trading.annotation.TransactionBoundary;

/**
 * Shared pointcut expressions for all service layer AOP advices.
 */
@Aspect
public class Pointcuts {
    /**
     * Pointcut expression matching all transactional boundaries in service layer.
     */
    @Pointcut("execution(public * io.roach.trading..*(..)) "
            + "&& @within(transactionBoundary) "
            + "|| @annotation(transactionBoundary)")
    public void anyTransactionBoundaryOperation(TransactionBoundary transactionBoundary) {
    }

    /**
     * Pointcut expression matching all retryable transactional boundaries in service layer.
     */
    @Pointcut("execution(public * io.roach.trading..*(..)) "
            + "&& @within(io.roach.trading.annotation.TransactionBoundary) "
            + "&& @annotation(retryable)")
    public void retryableBoundaryOperation(Retryable retryable) {
    }
}

