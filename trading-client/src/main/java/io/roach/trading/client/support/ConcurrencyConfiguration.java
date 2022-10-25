package io.roach.trading.client.support;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConcurrencyConfiguration {
    @Bean(destroyMethod = "shutdownNow")
    public ExecutorService executorService() {
        return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors() * 4);
    }
}
