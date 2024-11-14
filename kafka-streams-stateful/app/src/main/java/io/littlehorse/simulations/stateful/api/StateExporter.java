package io.littlehorse.simulations.stateful.api;

import io.javalin.Javalin;
import io.javalin.http.Context;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;

public class StateExporter implements Closeable {

    private final Javalin server;
    private final int port;
    private final KafkaStreams streams;
    private final String storeName;

    public StateExporter(final int port, final KafkaStreams app, final String storeName) {
        this.port = port;
        this.streams = app;
        this.storeName = storeName;
        server = Javalin.create().get("/state", this::state);
    }

    private void state(final Context ctx) {
        final Map<String, Integer> data = new HashMap<>();
        final State state = new State(streams.state().toString(), data);

        if (streams.state() == KafkaStreams.State.RUNNING) {
            final ReadOnlyKeyValueStore<String, Bytes> store = streams.store(StoreQueryParameters.fromNameAndType(storeName, QueryableStoreTypes.keyValueStore()));
            try (KeyValueIterator<String, Bytes> records = store.all()) {
                while (records.hasNext()) {
                    final KeyValue<String, Bytes> next = records.next();
                    data.put(next.key, next.value.get().length);
                }
            }
        }

        ctx.json(state);
    }

    public void start() {
        server.start(port);
    }

    @Override
    public void close() {
        server.stop();
    }
}
