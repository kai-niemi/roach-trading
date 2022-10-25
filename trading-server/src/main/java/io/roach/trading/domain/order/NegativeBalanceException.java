package io.roach.trading.domain.order;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.roach.trading.domain.common.BusinessException;

/**
 * Business exception thrown if an account has insufficient funds.
 */
@ResponseStatus(value = HttpStatus.PRECONDITION_FAILED, reason = "Negative balance")
public class NegativeBalanceException extends BusinessException {
    public NegativeBalanceException(UUID id) {
        super("Negative balance for account with ID: " + id);
    }
}
