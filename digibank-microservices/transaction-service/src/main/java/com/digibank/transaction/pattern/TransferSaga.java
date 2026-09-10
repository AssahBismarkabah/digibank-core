package com.digibank.transaction.pattern;

import java.util.function.BiConsumer;

public final class TransferSaga {
    public <T> T execute(T state, Runnable debit, Runnable persist, Runnable notify, BiConsumer<T, RuntimeException> compensate) {
        try {
            debit.run();
            persist.run();
            notify.run();
            return state;
        } catch (RuntimeException failure) {
            compensate.accept(state, failure);
            throw failure;
        }
    }
}
