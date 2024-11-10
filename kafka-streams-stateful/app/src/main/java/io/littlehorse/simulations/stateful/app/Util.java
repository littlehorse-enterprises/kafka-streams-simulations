package io.littlehorse.simulations.stateful.app;

import net.datafaker.Faker;
import net.datafaker.service.RandomService;
import org.apache.kafka.common.utils.Bytes;

public final class Util {
    private static final Faker FAKER = new Faker();

    private static final RandomService RANDOM = FAKER.random();

    private Util(){}

    public static Bytes randomBytes() {
        // random 10KB - 20KB
        return new Bytes(RANDOM.nextRandomBytes(RANDOM.nextInt(10000, 20000)));
    }

}
