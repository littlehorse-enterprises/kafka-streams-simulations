package kafka.streams.internals;

import net.datafaker.Faker;
import net.datafaker.service.RandomService;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.utils.Bytes;

import java.io.IOException;
import java.util.Properties;
import java.util.stream.Stream;

public class FakeDataGenerator {

    private static final Faker FAKER = new Faker();

    public static void main(String[] args) throws IOException {
        boolean fail = false;
        if (args.length > 0) {
            fail = true;
        }

        Properties properties = AppConfig.producerConfig(args);
        try(Producer<String, Bytes> producer = new KafkaProducer<>(properties)){
            if(!fail) {
                Stream.generate(FakeDataGenerator::newRecord)
                        .limit(200_000_000)
                        .forEach(producer::send);
            } else {
                producer.send(failSignal());
            }

        }
    }

    private static ProducerRecord<String, Bytes> newRecord(){
        RandomService random = FAKER.random();
        byte[] randomBytes = random.nextRandomBytes(5);
        String characterName = FAKER.starWars().character();
            return new ProducerRecord<>(App.INPUT_TOPIC, characterName, Bytes.wrap(randomBytes));
    }

    private static ProducerRecord<String, Bytes> failSignal() {
        RandomService random = FAKER.random();
        byte[] randomBytes = random.nextRandomBytes(5);
        return new ProducerRecord<>(App.INPUT_TOPIC, "fail", Bytes.wrap(randomBytes));
    }

}
