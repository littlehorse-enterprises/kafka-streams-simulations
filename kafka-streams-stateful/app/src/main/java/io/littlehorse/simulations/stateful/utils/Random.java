package io.littlehorse.simulations.stateful.utils;

import net.datafaker.Faker;
import net.datafaker.service.RandomService;
import org.apache.kafka.common.utils.Bytes;

public final class Random {
    private static final Faker FAKER = new Faker();

    private static final RandomService RANDOM = FAKER.random();

    private Random() {
    }

    /**
     * @return random 10KB - 20KB
     */
    public static Bytes bytes() {
        return new Bytes(RANDOM.nextRandomBytes(RANDOM.nextInt(10000, 20000)));
    }

    public static Bytes bytes(final int size) {
        return new Bytes(RANDOM.nextRandomBytes(size));
    }

    public static String key() {
        return FAKER.starWars().character();
    }

}
