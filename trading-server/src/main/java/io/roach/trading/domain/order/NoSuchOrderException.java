package io.roach.trading.domain.order;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.roach.trading.domain.common.BusinessException;

/**
 * Business exception thrown if a referenced order does not exist.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such order")
public class NoSuchOrderException extends BusinessException {
    public NoSuchOrderException(UUID id) {
        super("No order found with ID '" + id + "'");
    }

    public NoSuchOrderException(String ref) {
        super("No order found with reference '" + ref + "'");
    }
}
