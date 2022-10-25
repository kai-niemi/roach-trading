package io.roach.trading.client;

import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStyle;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.shell.jline.PromptProvider;

import io.roach.trading.client.support.ConnectionUpdatedEvent;

@Configuration
@EnableAutoConfiguration
@EnableConfigurationProperties
@ComponentScan(basePackageClasses = ClientApplication.class)
public class ClientApplication implements PromptProvider {
    public static void main(String[] args) {
        new SpringApplicationBuilder(ClientApplication.class)
                .web(WebApplicationType.NONE)
                .headless(false)
                .logStartupInfo(true)
                .run(args);
    }

    private String hostName;

    @EventListener
    public void handle(ConnectionUpdatedEvent event) {
        this.hostName = event.getBaseUri().getHost();
    }

    @Override
    public AttributedString getPrompt() {
        if (hostName != null) {
            return new AttributedString(hostName + ":$ ", AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN));
        } else {
            return new AttributedString("disconnected:$ ", AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));
        }
    }
}

