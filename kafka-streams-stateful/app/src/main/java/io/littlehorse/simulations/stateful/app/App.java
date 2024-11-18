package io.littlehorse.simulations.stateful.app;


import io.littlehorse.simulations.stateful.api.StateExporter;
import io.littlehorse.simulations.stateful.common.Config;
import io.littlehorse.simulations.stateful.kafka.TopicCreator;
import io.littlehorse.simulations.stateful.utils.Random;
import io.littlehorse.simulations.stateful.utils.ShutdownHook;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.StreamsBuilder;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.errors.StreamsUncaughtExceptionHandler;
import org.apache.kafka.streams.kstream.KStream;
import org.apache.kafka.streams.kstream.Materialized;
import org.apache.kafka.streams.state.KeyValueBytesStoreSupplier;
import org.apache.kafka.streams.state.KeyValueStore;
import org.apache.kafka.streams.state.StoreBuilder;
import org.apache.kafka.streams.state.Stores;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.CountDownLatch;

@Slf4j
@Command(name = "streams", description = "Kafka streams example app.")
public class App implements Runnable {

    private static final List<NewTopic> TOPICS = List.of(new NewTopic(Config.INPUT_TOPIC, 15, (short) 1), new NewTopic(Config.OUTPUT_TOPIC, 15, (short) 1));
    private static final String DSL_STORE_NAME = "dsl-store";
    private static final String PROCESSOR_STORE_NAME = "processor-state";
    @Option(names = {"-d", "--dsl"}, description = "Use DSL.")
    private boolean dsl;
    @Parameters(description = "Properties file path.")
    private File configurations;

    private static Topology buildDslTopology() {
        final StreamsBuilder builder = new StreamsBuilder();
        final KStream<String, Bytes> source = builder.stream(Config.INPUT_TOPIC);
        final Materialized<String, Bytes, KeyValueStore<Bytes, byte[]>> materialized = Materialized.
                <String, Bytes, KeyValueStore<Bytes, byte[]>>as(DSL_STORE_NAME)
                .withKeySerde(Serdes.String())
                .withValueSerde(Serdes.Bytes());
        source.groupByKey().aggregate(() -> new Bytes(new byte[0]), (key, value, aggregate) -> Random.bytes(), materialized);
        return builder.build();
    }

    private static Topology buildProcessorTopology() {
        final Topology topology = new Topology();
        final StoreBuilder<KeyValueStore<String, Bytes>> processorStore = Stores.keyValueStoreBuilder(
                Stores.persistentKeyValueStore(PROCESSOR_STORE_NAME), Serdes.String(), Serdes.Bytes());
        KeyValueBytesStoreSupplier inMemoryKeyValueStore = Stores.inMemoryKeyValueStore(PROCESSOR_STORE_NAME);
        topology.addSource("input", Serdes.String().deserializer(), Serdes.Bytes().deserializer(), Config.INPUT_TOPIC);
        topology.addProcessor("random-value-processor", RandomValueProcessor::new, "input");
        topology.addStateStore(processorStore, "random-value-processor");
//        topology.addStateStore(Stores.keyValueStoreBuilder(inMemoryKeyValueStore, Serdes.String(), Serdes.Bytes()), "random-value-processor");
        return topology;
    }

    @SneakyThrows
    @Override
    public void run() {
        final Properties props = Config.streams(configurations);
        try (TopicCreator topicCreator = new TopicCreator(props)) {
            topicCreator.create(TOPICS);
        }

        final Topology topology = dsl ? buildDslTopology() : buildProcessorTopology();
        final KafkaStreams streams = new KafkaStreams(topology, props);
        streams.setUncaughtExceptionHandler(exception -> StreamsUncaughtExceptionHandler.StreamThreadExceptionResponse.SHUTDOWN_CLIENT);
        streams.setGlobalStateRestoreListener(new AppRestoreListener());

        final StateExporter stateExporter = new StateExporter(Config.httpPort(configurations), streams, dsl ? DSL_STORE_NAME : PROCESSOR_STORE_NAME);

        final CountDownLatch latch = new CountDownLatch(2);
        ShutdownHook.add("API", () -> {
            stateExporter.close();
            latch.countDown();
        });
        ShutdownHook.add("Kafka Streams", () -> {
            streams.close();
            latch.countDown();
        });

        try {
            streams.start();
            stateExporter.start();
            latch.await();
            log.info("Done");
        } catch (final Throwable e) {
            log.error("{}", e.getMessage(), e);
            System.exit(1);
        }
    }
}
