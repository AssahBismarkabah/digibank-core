package com.digibank.transaction.pattern;

import org.junit.jupiter.api.Test;
import java.util.concurrent.atomic.AtomicBoolean;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TransferSagaTest {
    @Test
    void compensatesWhenNotificationFails() {
        AtomicBoolean compensated = new AtomicBoolean();
        TransferSaga saga = new TransferSaga();
        assertThrows(IllegalStateException.class, () -> saga.execute("transfer", () -> {}, () -> {}, () -> { throw new IllegalStateException(); }, (state, failure) -> compensated.set(true)));
        assertTrue(compensated.get());
    }
}
