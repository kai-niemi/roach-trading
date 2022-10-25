package io.roach.product;

import java.util.UUID;

import io.roach.product.domain.Product;
import io.roach.product.util.Money;

/**
 * Account and product test doubles.
 */
public abstract class Doubles {
    public static final Product APPLE_A
            = new Product(UUID.randomUUID(), "Apple A", Money.euro("30.50"), Money.euro("29.85"));

    public static final Product APPLE_B
            = new Product(UUID.randomUUID(), "Apple B", Money.euro("20.50"), Money.euro("19.50"));

    public static final Product APPLE_C
            = new Product(UUID.randomUUID(), "Apple C", Money.euro("10.50"), Money.euro("9.50"));

    public static final Product NOKIA_A
            = new Product(UUID.randomUUID(), "Nokia A", Money.euro("1.55"), Money.euro("1.60"));

    public static final Product NOKIA_B
            = new Product(UUID.randomUUID(), "Nokia B", Money.euro("1.56"), Money.euro("1.62"));

    public static final Product NOKIA_C
            = new Product(UUID.randomUUID(), "Nokia C", Money.euro("1.57"), Money.euro("1.63"));

    public static final Product MOTOROLA_A
            = new Product(UUID.randomUUID(), "Motorola A", Money.euro("4.65"), Money.euro("4.73"));

    public static final Product MOTOROLA_B
            = new Product(UUID.randomUUID(), "Motorola B", Money.euro("4.75"), Money.euro("4.83"));

    public static final Product MOTOROLA_C
            = new Product(UUID.randomUUID(), "Motorola C", Money.euro("4.85"), Money.euro("4.93"));

    public static final Product[] ALL_PRODUCTS = {
            Doubles.APPLE_A, Doubles.APPLE_B, Doubles.APPLE_C,
            Doubles.NOKIA_A, Doubles.NOKIA_B, Doubles.NOKIA_C,
            Doubles.MOTOROLA_A, Doubles.MOTOROLA_B, Doubles.MOTOROLA_C
    };

    private Doubles() {
    }
}
