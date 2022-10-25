package io.roach.product;

import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import io.roach.product.util.ExcludeFromTest;

@Configuration
@EnableAutoConfiguration(exclude = {
        TransactionAutoConfiguration.class,
        SecurityAutoConfiguration.class,
        DataSourceAutoConfiguration.class,
        DataSourceTransactionManagerAutoConfiguration.class,
        ErrorMvcAutoConfiguration.class,
        WebMvcAutoConfiguration.class,
})
@ComponentScan(basePackages = "io.roach")
@ServletComponentScan
@ExcludeFromTest
@EnableScheduling
public class ProductApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ProductApplication.class)
                .logStartupInfo(true)
                .web(WebApplicationType.SERVLET)
                .bannerMode(Banner.Mode.CONSOLE)
                .run(args);
    }
}

