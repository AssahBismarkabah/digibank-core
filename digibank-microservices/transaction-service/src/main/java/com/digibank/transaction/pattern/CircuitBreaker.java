package com.digibank.transaction.pattern;

import java.time.Duration;
import java.time.Instant;
import java.util.function.Supplier;

public final class CircuitBreaker {
    private final int failureThreshold;
    private final Duration resetTimeout;
    private int failures;
    private Instant openedAt;

    public CircuitBreaker(int failureThreshold, Duration resetTimeout) {
        this.failureThreshold = failureThreshold;
        this.resetTimeout = resetTimeout;
    }

    public synchronized <T> T execute(Supplier<T> action, Supplier<T> fallback) {
        if (isOpen()) return fallback.get();
        try {
            T result = action.get();
            failures = 0;
            return result;
        } catch (RuntimeException ex) {
            failures++;
            if (failures >= failureThreshold) openedAt = Instant.now();
            return fallback.get();
        }
    }

    private boolean isOpen() {
        return openedAt != null && Duration.between(openedAt, Instant.now()).compareTo(resetTimeout) < 0;
    }
}
