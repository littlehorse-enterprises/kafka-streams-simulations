package io.littlehorse.simulations.stateful.app;

import io.javalin.http.ContentType;
import io.javalin.http.Context;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.KafkaStreams;
import org.apache.kafka.streams.KeyValue;
import org.apache.kafka.streams.StoreQueryParameters;
import org.apache.kafka.streams.state.KeyValueIterator;
import org.apache.kafka.streams.state.QueryableStoreTypes;
import org.apache.kafka.streams.state.ReadOnlyKeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class HttpHandler {

    private final KafkaStreams streamsApp;

    private final NumberFormat format = NumberFormat.getInstance();
    private final Logger logger = LoggerFactory.getLogger(HttpHandler.class);
    private final String storeName;

    public HttpHandler(final KafkaStreams streamsApp, final boolean usesDsl) {
        this.streamsApp = streamsApp;
        storeName = usesDsl ? "dsl-state" : "processor-state";
    }

    public void state(Context ctx) {
        Map<String, String> currentState = new HashMap<>();
        StoreQueryParameters<ReadOnlyKeyValueStore<String, Bytes>> params =
                StoreQueryParameters.fromNameAndType(storeName, QueryableStoreTypes.keyValueStore());
        if(streamsApp.state() == KafkaStreams.State.RUNNING) {
            ReadOnlyKeyValueStore<String, Bytes> store = streamsApp.store(params);
            try(KeyValueIterator<String, Bytes> records = store.all()) {
                while (records.hasNext()) {
                    KeyValue<String, Bytes> next = records.next();
                    currentState.put(next.key, String.valueOf(next.value.get().length));
                }
            }
        }else {
            currentState.put("state", streamsApp.state().toString());
        }

        ctx.contentType(ContentType.APPLICATION_JSON);

            ctx.json(currentState);

    }

}
