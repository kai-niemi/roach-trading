package io.roach.trading.domain.changefeed;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.roach.trading.annotation.TransactionBoundary;

@RestController
@RequestMapping(value = "/api/cdc")
public class ChangeFeedController {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final AtomicInteger counter = new AtomicInteger(0);

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ChangeFeedListener changeFeedListener;

    @PostMapping(value = "/product", consumes = {MediaType.ALL_VALUE})
    @TransactionBoundary
    public ResponseEntity<?> productChangeEvent(@RequestBody String body) {
        logger.debug("productChangeEvent ({}): {}", counter.incrementAndGet(), body);

        try {
            ProductChangeEvent event = objectMapper.readerFor(ProductChangeEvent.class)
                    .readValue(body);
            if (!StringUtils.hasLength(event.getResolved())) {
                changeFeedListener.onProductChangeEvent(event.getPayload());
            }
        } catch (IOException e) {
            logger.warn("", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.toString());
        }

        return ResponseEntity.ok().build();
    }
}
