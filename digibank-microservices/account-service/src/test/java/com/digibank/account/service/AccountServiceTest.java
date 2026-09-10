package com.digibank.account.service;

import com.digibank.account.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AccountServiceTest {
    @Test
    void rejectsMissingAccountForBalanceUpdate() {
        AccountRepository repository = Mockito.mock(AccountRepository.class);
        Mockito.when(repository.findById(1L)).thenReturn(java.util.Optional.empty());
        assertThrows(jakarta.persistence.EntityNotFoundException.class, () -> new AccountService(repository).updateBalance(1L, java.math.BigDecimal.ONE, "CREDIT"));
    }
}
