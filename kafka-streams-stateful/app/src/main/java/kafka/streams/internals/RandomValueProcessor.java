package kafka.streams.internals;

import org.apache.kafka.common.errors.ProducerFencedException;
import org.apache.kafka.common.utils.Bytes;
import org.apache.kafka.streams.processor.api.Processor;
import org.apache.kafka.streams.processor.api.ProcessorContext;
import org.apache.kafka.streams.processor.api.Record;
import org.apache.kafka.streams.state.KeyValueStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.atomic.AtomicReference;

public class RandomValueProcessor implements Processor<String, Bytes, String, Bytes> {

    private KeyValueStore<String, Bytes> store;
    private final Logger logger = LoggerFactory.getLogger(HttpHandler.class);
    private ProcessorContext<String, Bytes> context;
    private static final AtomicReference<Long> lastFailure = new AtomicReference<>(0L);

    @Override
    public void init(ProcessorContext<String, Bytes> context) {
        store = context.getStateStore("processor-state");
        this.context = context;
    }

    @Override
    public void process(Record<String, Bytes> record) {
        if(record.key().equalsIgnoreCase("fail") && record.timestamp() > lastFailure.get()) {
            lastFailure.set(record.timestamp());
            throw new RuntimeException("Injected exception");
        }
        store.put(record.key(), record.value());
    }
}
