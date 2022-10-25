package io.roach.trading.domain.account;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.roach.trading.domain.common.BusinessException;

@ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "No such account")
public class NoSuchAccountException extends BusinessException {
    public NoSuchAccountException(String message) {
        super(message);
    }
}
