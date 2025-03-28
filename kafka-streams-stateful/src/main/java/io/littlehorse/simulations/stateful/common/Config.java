package io.littlehorse.simulations.stateful.common;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.BytesSerializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Config {

    public static final String INPUT_TOPIC = "input-topic";
    public static final String OUTPUT_TOPIC = "output-topic";
    private static final String SERVER_PORT = "server.port";
    private static final String STREAMS_CONFIG_PREFIX = "streams.";
    private static final String HTTP_CONFIG_PREFIX = "http.";

    private Config() {
    }

    public static Properties streams(final File file) throws IOException {
        final Properties props = filterByPrefix(loadFromFile(file), STREAMS_CONFIG_PREFIX);
        props.putIfAbsent(StreamsConfig.CLIENT_ID_CONFIG, "my-app");
        props.putIfAbsent(StreamsConfig.APPLICATION_ID_CONFIG, "streams-number-aggregation-demo");
        props.putIfAbsent(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        props.putIfAbsent(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.putIfAbsent(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.Bytes().getClass().getName());
        props.putIfAbsent(StreamsConfig.STATE_DIR_CONFIG, "/tmp/streams-1");
        props.putIfAbsent(StreamsConfig.NUM_STANDBY_REPLICAS_CONFIG, 1);
        props.putIfAbsent(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE_V2);
        props.putIfAbsent(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 10);
        return props;
    }

    private static Properties filterByPrefix(final Properties props, final String prefix) {
        final Properties out = new Properties();
        props.entrySet().stream()
                .filter(configEntry -> configEntry.getKey().toString().startsWith(prefix))
                .forEach(validConfigEntry -> out.put(validConfigEntry.getKey().toString().replace(prefix, ""), validConfigEntry.getValue()));
        return out;
    }

    public static int httpPort(final File file) throws IOException {
        final Properties props = filterByPrefix(loadFromFile(file), HTTP_CONFIG_PREFIX);
        props.putIfAbsent(SERVER_PORT, "8080");
        return Integer.parseInt(props.get(SERVER_PORT).toString());
    }

    private static Properties loadFromFile(final File file) throws IOException {
        final Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream(file)) {
            props.load(fis);
        }
        return props;
    }

    public static Properties producer(final File file) throws IOException {
        final Properties props = loadFromFile(file);
        props.putIfAbsent(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:19092");
        props.putIfAbsent(ProducerConfig.CLIENT_ID_CONFIG, "my-app");
//        props.putIfAbsent(ProducerConfig.PARTITIONER_IGNORE_KEYS_CONFIG, "true");
        props.putIfAbsent(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.putIfAbsent(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, BytesSerializer.class);
        return props;
    }

}
