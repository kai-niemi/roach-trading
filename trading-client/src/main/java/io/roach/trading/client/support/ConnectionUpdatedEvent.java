package io.roach.trading.client.support;

import java.net.URI;

import org.springframework.context.ApplicationEvent;

public class ConnectionUpdatedEvent extends ApplicationEvent {
    private URI baseUri;

    private String version;

    public ConnectionUpdatedEvent(Object source, URI baseUri, String version) {
        super(source);
        this.baseUri = baseUri;
        this.version = version;
    }

    public URI getBaseUri() {
        return baseUri;
    }

    public String getVersion() {
        return version;
    }
}
