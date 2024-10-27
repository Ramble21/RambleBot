package com.github.Ramble21.classes;

import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.TimeUnit;

public final class Stopwatch {

    private Instant startTime;
    private Instant endTime;
    private boolean running;

    public void start() {
        if (!running) {
            startTime = Instant.now();
            running = true;
        } else {
            System.out.println("Stopwatch is already running. Use stop() before starting again.");
        }
    }

    public void stop() {
        if (running) {
            endTime = Instant.now();
            running = false;
        } else {
            System.out.println("Stopwatch is not running. Use start() before stopping.");
        }
    }

    public Duration getElapsedDuration() {
        if (running) {
            return Duration.between(startTime, Instant.now());
        } else {
            return Duration.between(startTime, endTime);
        }
    }

    public int getElapsedTime() {
        Duration duration = getElapsedDuration();
        return (int)duration.toMillis();
    }

    public int getElapsedTime(TimeUnit timeUnit) {
        return (int)(timeUnit.convert(getElapsedTime(), TimeUnit.MILLISECONDS));
    }
}
