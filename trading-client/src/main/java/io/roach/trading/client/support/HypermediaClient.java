package io.roach.trading.client.support;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.MediaTypes;
import org.springframework.hateoas.client.Traverson;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class HypermediaClient {
    public static String withCurie(String rel) {
        return "trading:" + rel;
    }

    private static final List<MediaType> ACCEPT_TYPES = Arrays.asList(
            MediaTypes.HAL_JSON, MediaTypes.HAL_FORMS_JSON);

    @Autowired
    private RestTemplate restTemplate;

    private URI baseUri;

    public HypermediaClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @EventListener
    public void handle(ConnectionUpdatedEvent event) {
        this.baseUri = event.getBaseUri();
    }

    public Traverson fromRoot() {
        return from(baseUri);
    }

    public Traverson.TraversalBuilder follow(Optional<Link> link) {
        return from(link.get().toUri()).follow();
    }

    private Traverson from(URI uri) {
        Traverson traverson = new Traverson(uri, ACCEPT_TYPES);
        traverson.setRestOperations(restTemplate);
        return traverson;
    }

    public ResponseEntity<String> post(Link link) {
        return restTemplate.postForEntity(link.getTemplate().expand(), null, String.class);
    }

    public <T> ResponseEntity<T> post(Link link, Class<T> responseType) {
        return restTemplate.postForEntity(
                link.getTemplate().expand(),
                null,
                responseType);
    }

    public <T> ResponseEntity<T> post(Link link, Object request, Class<T> responseType) {
        return restTemplate.postForEntity(
                link.getTemplate().expand(),
                request,
                responseType);
    }

    public ResponseEntity<String> get(Link link) {
        return restTemplate.getForEntity(link.toUri(), String.class);
    }
}
