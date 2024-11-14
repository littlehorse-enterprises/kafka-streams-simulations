package io.littlehorse.simulations.stateful.kafka;

import io.littlehorse.simulations.stateful.utils.ShutdownHook;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.common.errors.TopicExistsException;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

@Slf4j
public class TopicCreator implements Closeable {

    private final AdminClient adminClient;

    public TopicCreator(final Properties configurations) {
        adminClient = AdminClient.create(configurations);
        ShutdownHook.add(this);
    }

    @SneakyThrows
    public void create(final List<NewTopic> topics) {
        for (NewTopic newTopic : topics) {
            try {
                adminClient.createTopics(List.of(newTopic)).all().get(30, TimeUnit.SECONDS);
                log.info("Topic {} created", newTopic);
            } catch (Exception e) {
                if (e.getCause() instanceof TopicExistsException) {
                    log.warn(e.getMessage());
                } else {
                    throw e;
                }
            }
        }

    }

    @Override
    public void close() {
        adminClient.close();
    }
}
