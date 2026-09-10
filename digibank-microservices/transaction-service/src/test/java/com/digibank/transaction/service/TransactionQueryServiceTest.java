package com.digibank.transaction.service;

import com.digibank.transaction.model.Transaction;
import com.digibank.transaction.repository.TransactionRepository;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class TransactionQueryServiceTest {

    @Test
    void readsByIdThroughQueryService() {
        TransactionRepository repository = mock(TransactionRepository.class);
        Transaction transaction = new Transaction(7L, BigDecimal.TEN, "DEPOSIT", "Opening deposit");
        when(repository.findById(1L)).thenReturn(Optional.of(transaction));

        var response = new TransactionQueryService(repository).findById(1L);

        assertThat(response.accountId()).isEqualTo(7L);
        assertThat(response.amount()).isEqualByComparingTo(BigDecimal.TEN);
    }
}
