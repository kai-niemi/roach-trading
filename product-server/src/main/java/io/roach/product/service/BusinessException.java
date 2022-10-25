package io.roach.product.service;

/**
 * Base type for usually recoverable Trading System business exceptions.
 */
public abstract class BusinessException extends RuntimeException {
    protected BusinessException(String message) {
        super(message);
    }
}
