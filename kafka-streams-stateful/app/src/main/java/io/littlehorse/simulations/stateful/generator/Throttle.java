package io.littlehorse.simulations.stateful.generator;

import io.littlehorse.simulations.stateful.utils.ShutdownHook;
import lombok.extern.slf4j.Slf4j;

import java.io.Closeable;
import java.time.Duration;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
public class Throttle implements Closeable, Runnable {

    private final AtomicBoolean awaiting = new AtomicBoolean();
    private final Thread executor = new Thread(this);

    private final Duration sleep;
    private final Duration span;

    public Throttle(Duration span, Duration sleep) {
        ShutdownHook.add(this);

        this.sleep = sleep;
        this.span = span;

        executor.start();
    }

    public void await(final Runnable callback) {
        if (awaiting.get()) {
            for (; ; ) {
                if (!awaiting.get()) {
                    break;
                }
            }
            callback.run();
        }
    }


    @Override
    public void close() {
        executor.interrupt();
    }

    @Override
    public void run() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                Thread.sleep(span);
                awaiting.set(true);
                log.info("Starting throttling");
                Thread.sleep(sleep);
                log.info("Ending throttling");
                awaiting.set(false);
            } catch (InterruptedException e) {
                log.info("Closing");
                break;
            }
        }
    }
}
