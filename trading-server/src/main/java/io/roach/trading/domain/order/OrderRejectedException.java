package io.roach.trading.domain.order;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

import io.roach.trading.domain.common.BusinessException;

/**
 * Exception thrown if an order is rejected by a violation of market rules. Usually by
 * setting a product sell price that is 5% over market price or a buy order that
 * is 5% below market price.
 */
@ResponseStatus(value = HttpStatus.PRECONDITION_FAILED, reason = "Rule violation")
public class OrderRejectedException extends BusinessException {
    public OrderRejectedException(String message) {
        super(message);
    }
}
