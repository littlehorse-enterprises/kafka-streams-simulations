package io.littlehorse.simulations.stateful.app;

import io.littlehorse.simulations.stateful.utils.Random;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueStore;

@Slf4j
public class RandomValueProcessor implements Processor<String, Bytes, String, Bytes> {

    private KeyValueStore<String, Bytes> store;
    private ProcessorContext<String, Bytes> context;

    @Override
    public void init(ProcessorContext<String, Bytes> context) {
        store = context.getStateStore("processor-state");
        this.context = context;
    }

    @Override
    public void process(Record<String, Bytes> record) {
        long recordLatency = System.currentTimeMillis() - record.timestamp();
        if (record.key().equalsIgnoreCase("fail") && recordLatency <= 50) {
            throw new RuntimeException("Injected exception");
        }
        store.put(record.key(), Random.bytes());
    }
}
