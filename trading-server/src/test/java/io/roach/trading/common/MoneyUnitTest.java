package io.roach.trading.common;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static io.roach.trading.api.support.Money.kronor;

public class MoneyUnitTest {
    @Test
    public void binaryArithmetics() {
        Assertions.assertEquals(
                kronor("100.00"),
                kronor("80.00").plus(kronor("20.00")));

        Assertions.assertEquals(
                kronor("19.50"),
                kronor("10.05").plus(kronor("9.95"), kronor("-0.50")));

        Assertions.assertEquals(
                kronor("80.00"),
                kronor("100.00").minus(kronor("20.00")));

        Assertions.assertEquals(
                kronor("100.00"),
                kronor("10.00").multiply(10));

        Assertions.assertEquals(
                kronor("20.00"),
                kronor("100.00").divide(5));

        Assertions.assertEquals(
                kronor("0.00"),
                kronor("100.00").remainder(100));

        Assertions.assertEquals(
                kronor("100.00"),
                kronor("100.00").max(kronor("80.00")));

        Assertions.assertEquals(
                kronor("80.00"),
                kronor("100.00").min(kronor("80.00")));

        Assertions.assertTrue(kronor("110.00").isGreaterThan(kronor("100.00")));
        Assertions.assertTrue((kronor("100.00").isGreaterThanOrEqualTo(kronor("100.00"))));
        Assertions.assertTrue(kronor("99.00").isLessThan(kronor("100.00")));
        Assertions.assertTrue(kronor("100.00").isLessThanOrEqualTo(kronor("100.00")));
        Assertions.assertTrue(kronor("100.00").isSameCurrency(kronor("100.00")));
    }

    @Test
    public void unaryArithmetics() {
        Assertions.assertEquals(
                kronor("-100.00"),
                kronor("100.00").negate());

        Assertions.assertTrue(kronor("-100.00").isNegative());
        Assertions.assertTrue(kronor("+100.00").isPositive());
        Assertions.assertTrue(kronor("-0.00").isZero());
        Assertions.assertTrue(kronor("0.00").isZero());
        Assertions.assertTrue(kronor("+0.00").isZero());
    }

    @Test
    public void failOnMalformedAmount() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            kronor("0");
        });
    }

    @Test
    public void failOnWrongFractionDigits() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            kronor("0.0");
        });
    }

    @Test
    public void failOnWrongFractionDigits2() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> {
            kronor("0.000");
        });
    }
}
