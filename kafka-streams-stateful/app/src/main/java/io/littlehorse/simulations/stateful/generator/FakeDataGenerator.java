package io.littlehorse.simulations.stateful.generator;

import io.littlehorse.simulations.stateful.common.AppConfig;
import net.datafaker.Faker;
import net.datafaker.service.RandomService;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.utils.Bytes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

public class FakeDataGenerator {

    private static final Faker FAKER = new Faker();
    private static final AtomicLong incrementer = new AtomicLong(0);
    private static final AtomicLong lastLogTime = new AtomicLong(0);
    private static final Logger log = LoggerFactory.getLogger(FakeDataGenerator.class);

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
                        .forEach(record -> {
                            producer.send(record, (recordMetadata, e) -> incrementer.incrementAndGet());
                        });
            } else {
                producer.send(failSignal());
            }

        }
    }

    private static ProducerRecord<String, Bytes> newRecord(){
        RandomService random = FAKER.random();
        byte[] randomBytes = random.nextRandomBytes(5);
        String characterName = FAKER.starWars().character();
        if((System.currentTimeMillis() - lastLogTime.get()) > 5000) {
            lastLogTime.set(System.currentTimeMillis());
            log.info(incrementer.get() + " Records sent");
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {}
        }
        incrementer.incrementAndGet();
        return new ProducerRecord<>(AppConfig.INPUT_TOPIC, characterName, Bytes.wrap(randomBytes));
    }

    private static ProducerRecord<String, Bytes> failSignal() {
        RandomService random = FAKER.random();
        byte[] randomBytes = random.nextRandomBytes(5);
        return new ProducerRecord<>(AppConfig.INPUT_TOPIC, "fail", Bytes.wrap(randomBytes));
    }

}
