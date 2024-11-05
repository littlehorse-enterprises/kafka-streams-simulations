package kafka.streams.internals;

import net.datafaker.Faker;
import net.datafaker.service.RandomService;
import org.apache.kafka.common.utils.Bytes;

public final class Util {
    private static final Faker FAKER = new Faker();

    private static final RandomService RANDOM = FAKER.random();

    private Util(){}

    public static Bytes randomBytes() {
        return new Bytes(RANDOM.nextRandomBytes(RANDOM.nextInt(10000, 20000)));
    }

}
