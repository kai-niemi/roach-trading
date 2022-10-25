package io.roach.trading.domain.account;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Business exception thrown if a referenced account does not exist.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such system account")
public class NoSuchSystemAccountException extends NoSuchAccountException {
    public NoSuchSystemAccountException(UUID id) {
        super("No system account with ID " + id);
    }
    
    public NoSuchSystemAccountException(UUID id, UUID refId) {
        super("No system account with ID " + id + " for trading account ID: " + refId);
    }
}
