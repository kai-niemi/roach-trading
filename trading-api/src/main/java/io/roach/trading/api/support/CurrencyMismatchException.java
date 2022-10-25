package io.roach.trading.api.support;

/**
 * Exception thrown on money calculation with conflicting currencies.
 */
public class CurrencyMismatchException extends IllegalArgumentException {
    public CurrencyMismatchException(String s) {
        super(s);
    }
}
