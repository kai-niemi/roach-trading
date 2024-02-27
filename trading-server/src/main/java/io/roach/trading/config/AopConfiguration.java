package io.roach.trading.config;

import io.roach.trading.ProfileNames;
import io.roach.trading.aspect.TransactionDecoratorAspect;
import io.roach.trading.aspect.TransactionRetryAspect;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * Configuration for all cross-cutting AOP aspects.
 */
@Configuration
public class AopConfiguration {
//    @Bean
//    public TransactionRetryAspect retryableOperationAspect() {
//        return new TransactionRetryAspect();
//    }

//    @Bean
//    @Profile("!(" + ProfileNames.PSQL_DEV_RC + "," + ProfileNames.PSQL_DEV + ")")
//    public TransactionDecoratorAspect transactionHintsAspect() {
//        return new TransactionDecoratorAspect();
//    }
}
