package io.roach.trading.domain.portfolio;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.roach.trading.domain.common.BusinessException;

/**
 * Business exception thrown if a referenced portfolio does not exist.
 */
@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such portfolio")
public class NoSuchPortfolioException extends BusinessException {
    public NoSuchPortfolioException(UUID id) {
        super("No portfolio found for account with ID: " + id);
    }
}

