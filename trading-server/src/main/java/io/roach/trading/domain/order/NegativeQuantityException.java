package io.roach.trading.domain.order;


import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.roach.trading.domain.common.BusinessException;

@ResponseStatus(value = HttpStatus.PRECONDITION_FAILED, reason = "Negative quantity")
public class NegativeQuantityException extends BusinessException {
    public NegativeQuantityException(String message) {
        super(message);
    }
}