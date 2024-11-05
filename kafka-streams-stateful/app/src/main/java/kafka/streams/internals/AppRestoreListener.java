package kafka.streams.internals;

import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.streams.processor.StateRestoreListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AppRestoreListener implements StateRestoreListener {
    private final Logger log = LoggerFactory.getLogger(AppRestoreListener.class);

    @Override
    public void onRestoreStart(TopicPartition topicPartition, String storeName, long startingOffset, long endingOffset) {
        log.info("Starting restoration. StoreName " + storeName);
    }

    @Override
    public void onBatchRestored(TopicPartition topicPartition, String storeName, long batchEndOffset, long numRestored) {
        log.info("Batch restored. StoreName %s Restored records= %s".formatted(storeName, numRestored));
    }

    @Override
    public void onRestoreEnd(TopicPartition topicPartition, String storeName, long totalRestored) {
        log.info("Restore end. StoreName %s".formatted(storeName));
    }
}
