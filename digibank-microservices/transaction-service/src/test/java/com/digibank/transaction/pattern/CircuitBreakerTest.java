package com.digibank.transaction.pattern;

import org.junit.jupiter.api.Test;
import java.time.Duration;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CircuitBreakerTest {
    @Test
    void opensAfterRepeatedFailuresAndUsesFallback() {
        CircuitBreaker breaker = new CircuitBreaker(1, Duration.ofMinutes(1));
        assertEquals("fallback", breaker.execute(() -> { throw new IllegalStateException(); }, () -> "fallback"));
        assertEquals("fallback", breaker.execute(() -> "live", () -> "fallback"));
    }
}
