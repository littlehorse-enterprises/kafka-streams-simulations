package io.littlehorse.simulations.stateful.app;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.streams.processor.StateRestoreListener;

@Slf4j
public class AppRestoreListener implements StateRestoreListener {

    @Override
    public void onRestoreStart(TopicPartition topicPartition, String storeName, long startingOffset, long endingOffset) {
        log.debug("Starting restoration. StoreName {}", storeName);
    }

    @Override
    public void onBatchRestored(TopicPartition topicPartition, String storeName, long batchEndOffset, long numRestored) {
        log.debug("Batch restored. StoreName {} restored {} records", storeName, numRestored);
    }

    @Override
    public void onRestoreEnd(TopicPartition topicPartition, String storeName, long totalRestored) {
        log.debug("Restore end. StoreName {}", storeName);
    }
}
