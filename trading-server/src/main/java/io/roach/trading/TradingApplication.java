package io.roach.trading;

import org.springframework.boot.Banner;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import io.roach.trading.util.ExcludeFromTest;

@Configuration
@EnableAutoConfiguration(exclude = {
        SecurityAutoConfiguration.class
})
@ComponentScan(basePackageClasses = TradingApplication.class)
@ExcludeFromTest
public class TradingApplication {
    public static void main(String[] args) {
        new SpringApplicationBuilder(TradingApplication.class)
                .logStartupInfo(true)
                .web(WebApplicationType.SERVLET)
                .bannerMode(Banner.Mode.CONSOLE)
                .run(args);
    }
}

