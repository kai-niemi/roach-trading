package io.roach.trading.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Profile;

import io.roach.trading.ProfileNames;
import io.roach.trading.aspect.RetryableOperationAspect;
import io.roach.trading.aspect.TransactionHintsAspect;

/**
 * Configuration for all cross-cutting AOP aspects.
 */
@Configuration
@EnableAspectJAutoProxy(proxyTargetClass = true)
public class AopConfiguration {
    @Bean
    public RetryableOperationAspect retryableOperationAspect() {
        return new RetryableOperationAspect();
    }

    @Bean
    @Profile("!(" + ProfileNames.PSQL_TEST + "," + ProfileNames.PSQL_DEV + ")")
    public TransactionHintsAspect transactionHintsAspect() {
        return new TransactionHintsAspect();
    }
}
