package io.roach.trading.domain.account;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such trading account")
public class NoSuchTradingAccountException extends NoSuchAccountException {
    public NoSuchTradingAccountException(UUID id) {
        super("No trading account with ID: " + id);
    }
}
