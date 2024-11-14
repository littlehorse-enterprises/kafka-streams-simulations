package io.littlehorse.simulations.stateful.generator;

import io.littlehorse.simulations.stateful.common.Config;
import io.littlehorse.simulations.stateful.utils.Random;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.utils.Bytes;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Stream;

@Slf4j
@Command(name = "generator", description = "Generate fake messages.")
public class Generator implements Runnable {

    @Option(names = {"-l", "--limit"}, defaultValue = "200000000", description = "Number of messages to produce. Default: ${DEFAULT-VALUE}.")
    private long limit;
    @Option(names = {"-f", "--fail"}, description = "Send a fail message.")
    private boolean fail;
    @Parameters(description = "Properties file path.")
    private File configurations;

    private ProducerRecord<String, Bytes> newRecord() {
        return new ProducerRecord<>(Config.INPUT_TOPIC, Random.key(), Random.bytes(5));
    }

    private ProducerRecord<String, Bytes> failSignal() {
        return new ProducerRecord<>(Config.INPUT_TOPIC, "fail", Random.bytes(5));
    }

    @SneakyThrows
    @Override
    public void run() {
        try (Producer<String, Bytes> producer = new KafkaProducer<>(Config.producer(configurations))) {
            if (fail) {
                producer.send(failSignal());
            } else {
                try (Throttle throttle = new Throttle(Duration.ofSeconds(3), Duration.ofSeconds(10))) {
                    final AtomicLong incrementer = new AtomicLong(0);
                    Stream.generate(this::newRecord)
                            .limit(limit)
                            .forEach(record -> {
                                producer.send(record);
                                log.debug("Produced messages: {}", incrementer.incrementAndGet());
                                throttle.await(() -> log.info("Produced messages so far: {}", incrementer.get()));
                            });
                }
            }
        }
    }
}
