package io.littlehorse.simulations.stateful.utils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ShutdownHook {
    private ShutdownHook() {}

    public static void add(final AutoCloseable closeable) {
        add(closeable.getClass().getSimpleName(), closeable);
    }

    public static void add(final String name, final AutoCloseable closeable) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            try {
                closeable.close();
            } catch (Exception e) {
                log.error("Error in ShutdownHook '{}'", name, e);
            }
            log.trace("Shutdown process for '{}' was completed", name);
        }));
    }
}
