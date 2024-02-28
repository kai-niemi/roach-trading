package io.roach.trading.util;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public abstract class RandomData {
    private RandomData() {
    }

    public static <E> E selectRandom(List<E> collection) {
        if (collection.isEmpty()) {
            throw new IllegalArgumentException("Empty collection");
        }
        return collection.get(ThreadLocalRandom.current().nextInt(collection.size()));
    }

}
