package io.roach.trading.client.commands;

import java.net.URI;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.hateoas.client.Traverson;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.shell.standard.ShellCommandGroup;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import org.springframework.web.client.RestTemplate;

import io.roach.trading.client.support.ConnectionUpdatedEvent;

@ShellComponent
@ShellCommandGroup("admin")
public class Connect extends BaseCommand {
    public static final String DEFAULT_URL = "http://localhost:8090/api/";

    private static boolean connected;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ApplicationEventPublisher applicationEventPublisher;

    public static boolean isConnected() {
        return connected;
    }

    @ShellMethod(value = "Connect to API endpoint", key = {"connect", "c"})
    public void connect(@ShellOption(value = {"--url", "-u"},
            help = "REST API base URI", defaultValue = DEFAULT_URL) String baseUrl) {

        logger.info("Connecting to {}..", baseUrl);

        ResponseEntity<String> entity = restTemplate.getForEntity(baseUrl, String.class);

        if (entity.getStatusCode() == HttpStatus.OK) {
            Connect.connected = true;

            ResponseEntity<String> response = new Traverson(URI.create(baseUrl), MediaType.APPLICATION_JSON)
                    .follow().toEntity(String.class);
            Map<String, String> header = response.getHeaders().toSingleValueMap();

            String name = header.get("X-Application-Context");
            if ("Roach Trading".equals(name)) {
                String version = header.get("X-Application-Version");
                logger.info("Connected to {} {}", name, version);
                logger.info("Type help for commands.");

                applicationEventPublisher.publishEvent(
                        new ConnectionUpdatedEvent(this, URI.create(baseUrl), version));
            } else {
                logger.warn("This doesnt look like Roach Trading API - please check URL!");
                logger.warn(response.getBody());
            }
        } else {
            logger.error("Connection failed: {}", entity.getStatusCode().getReasonPhrase());
        }
    }

}
