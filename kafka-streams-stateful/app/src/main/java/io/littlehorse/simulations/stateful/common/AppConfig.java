package io.littlehorse.simulations.stateful.common;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.BytesSerializer;
import org.apache.kafka.common.serialization.Serdes;
import org.apache.kafka.common.serialization.StringSerializer;
import org.apache.kafka.streams.StreamsConfig;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;

public class AppConfig {

    private static final String STREAMS_CONFIG_PREFIX = "streams.";
    private static final String HTTP_CONFIG_PREFIX = "http.";
    public static final String INPUT_TOPIC = "input-topic";
    public static final String OUTPUT_TOPIC = "output-topic";

    private AppConfig(){}

    public static Properties streamsConfig(String[] appArgs) throws IOException {
        Properties props = new Properties();
        if (appArgs != null && appArgs.length > 0) {
            try (final FileInputStream fis = new FileInputStream(Paths.get(appArgs[0]).toFile())) {
                props.load(fis);
            }
            if (appArgs.length > 1) {
                System.out.println("Warning: Some command line arguments were ignored. This demo only accepts an optional configuration file.");
            }
        }
        props = filterByPrefix(props, STREAMS_CONFIG_PREFIX);
        props.putIfAbsent(StreamsConfig.CLIENT_ID_CONFIG, "my-app");
        props.putIfAbsent(StreamsConfig.APPLICATION_ID_CONFIG, "streams-number-aggregation-demo");
        props.putIfAbsent(StreamsConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:9092");
        props.putIfAbsent(StreamsConfig.CACHE_MAX_BYTES_BUFFERING_CONFIG, 0);
        props.putIfAbsent(StreamsConfig.DEFAULT_KEY_SERDE_CLASS_CONFIG, Serdes.String().getClass().getName());
        props.putIfAbsent(StreamsConfig.DEFAULT_VALUE_SERDE_CLASS_CONFIG, Serdes.Bytes().getClass().getName());
        props.putIfAbsent(StreamsConfig.STATE_DIR_CONFIG, "/tmp/streams-1");
        props.putIfAbsent(StreamsConfig.NUM_STANDBY_REPLICAS_CONFIG, 1);
        props.putIfAbsent(StreamsConfig.COMMIT_INTERVAL_MS_CONFIG, 500);
        props.putIfAbsent(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
        props.putIfAbsent(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, 6000);
        props.putIfAbsent(ConsumerConfig.HEARTBEAT_INTERVAL_MS_CONFIG, 360);
        props.putIfAbsent(StreamsConfig.PROCESSING_GUARANTEE_CONFIG, StreamsConfig.EXACTLY_ONCE_V2);
        props.putIfAbsent(StreamsConfig.NUM_STREAM_THREADS_CONFIG, 10);
        return props;
    }

    public static Properties httpConfig(String[] appArgs) throws IOException {
        Properties props = new Properties();
        if (appArgs != null && appArgs.length > 0) {
            try (final FileInputStream fis = new FileInputStream(Paths.get(appArgs[0]).toFile())) {
                props.load(fis);
            }
            if (appArgs.length > 1) {
                System.out.println("Warning: Some command line arguments were ignored. This demo only accepts an optional configuration file.");
            }
        }
        props = filterByPrefix(props, HTTP_CONFIG_PREFIX);
        props.putIfAbsent("server.port", "8080");
        return props;
    }

    private static Properties filterByPrefix(Properties props, String prefix) {
        Properties out = new Properties();
        props.entrySet().stream()
                .filter(configEntry -> configEntry.getKey().toString().startsWith(prefix))
                .forEach(validConfigEntry -> {
                    out.put(validConfigEntry.getKey().toString().replace(prefix, ""), validConfigEntry.getValue());
                });
        return out;
    }

    public static int httpPort(Properties appArgs) {
        return Integer.parseInt(appArgs.get("server.port").toString());
    }

    public static boolean usesDsl(String[] appArgs) {
        if (appArgs != null && appArgs.length > 2) {
            String arg = appArgs[2];
            if(!arg.equalsIgnoreCase("dsl") && !arg.equalsIgnoreCase("processor")) {
                throw new IllegalArgumentException("Only dsl or processor arguments are supported.");
            }
            return arg.equalsIgnoreCase("dsl");
        } else {
            return false;
        }
    }

    public static Properties producerConfig(String[] appArgs) throws IOException{
        final Properties props = new Properties();
//        if (appArgs != null && appArgs.length > 0) {
//            try (final FileInputStream fis = new FileInputStream(appArgs[0])) {
//                props.load(fis);
//            }
//            if (appArgs.length > 3) {
//                System.out.println("Warning: Some command line arguments were ignored. This demo only accepts an optional configuration file.");
//            }
//        }
        props.putIfAbsent(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "127.0.0.1:19092");
        props.putIfAbsent(ProducerConfig.CLIENT_ID_CONFIG, "my-app");
        props.putIfAbsent(ProducerConfig.PARTITIONER_IGNORE_KEYS_CONFIG, "true");
        //props.putIfAbsent(ProducerConfig.LINGER_MS_CONFIG, "50");
        //props.putIfAbsent(ProducerConfig.BATCH_SIZE_CONFIG, "1000000");
        props.putIfAbsent(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.putIfAbsent(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, BytesSerializer.class.getName());
        return props;
    }

}
