package io.roach.trading.api.support;

import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public abstract class RandomUtils {
    private RandomUtils() {
    }

    public static final ThreadLocalRandom random = ThreadLocalRandom.current();

    private static final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();

    public static Money randomMoneyBetween(double low, double high, Currency currency) {
        return Money.of(String.format(Locale.US, "%.2f", random.nextDouble(low, high)), currency);
    }

    public static String randomString(int min) {
        byte[] buffer = new byte[min];
        random.nextBytes(buffer);
        return encoder.encodeToString(buffer);
    }

    public static <E> E selectRandom(E[] collection) {
        return selectRandom(Arrays.asList(collection));
    }

    public static <E> E selectRandom(Collection<E> collection) {
        List<E> givenList = new ArrayList<>(collection);
        return givenList.get(new SecureRandom().nextInt(givenList.size()));
    }

    @SuppressWarnings("unchecked")
    public static <K> K selectRandom(Set<K> set) {
        Object[] keys = set.toArray();
        return (K) keys[random.nextInt(keys.length)];
    }

    @SuppressWarnings("unchecked")
    public static <K, V> K selectRandom(Map<K, V> set) {
        Object[] keys = set.keySet().toArray();
        return (K) keys[random.nextInt(keys.length)];
    }
}
