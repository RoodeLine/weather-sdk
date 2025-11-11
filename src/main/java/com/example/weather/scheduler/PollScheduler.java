package com.example.weather.scheduler;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class PollScheduler implements AutoCloseable {
    private final ScheduledExecutorService exec;

    public PollScheduler() {
        this.exec = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "weather-poll-scheduler");
            t.setDaemon(true);
            return t;
        });
    }

    public ScheduledFuture<?> scheduleAtFixedRate(Runnable task, Duration initialDelay, Duration period) {
        Objects.requireNonNull(task, "task");
        return exec.scheduleAtFixedRate(task,
                Math.max(0, initialDelay.toMillis()),
                Math.max(1, period.toMillis()),
                TimeUnit.MILLISECONDS);
    }

    @Override
    public void close() {
        exec.shutdownNow();
    }
}
