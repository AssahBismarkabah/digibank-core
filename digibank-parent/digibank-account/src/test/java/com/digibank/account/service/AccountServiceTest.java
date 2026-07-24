package com.digibank.account.service;

import com.digibank.account.dto.AccountRequest;
import com.digibank.account.dto.AccountResponse;
import com.digibank.account.model.Account;
import com.digibank.account.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    private Account accountWithId(Long id, String accountNumber, BigDecimal balance,
                                  Long customerId, String accountType, String currency) {
        var account = new Account(accountNumber, balance, customerId, accountType, currency);
        account.setId(id);
        return account;
    }

    @Test
    void shouldCreateAccount() {
        var request = new AccountRequest();
        request.setAccountNumber("ACC001");
        request.setBalance(new BigDecimal("1000.00"));
        request.setCustomerId(1L);
        request.setAccountType("SAVINGS");
        request.setCurrency("USD");

        var saved = accountWithId(1L, "ACC001", new BigDecimal("1000.00"), 1L, "SAVINGS", "USD");
        given(accountRepository.existsByAccountNumber("ACC001")).willReturn(false);
        given(accountRepository.save(any(Account.class))).willReturn(saved);

        AccountResponse response = accountService.create(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getAccountNumber()).isEqualTo("ACC001");
        assertThat(response.getBalance()).isEqualByComparingTo(new BigDecimal("1000.00"));
        assertThat(response.getCustomerId()).isEqualTo(1L);
        then(accountRepository).should().save(any(Account.class));
    }

    @Test
    void shouldRejectDuplicateAccountNumber() {
        var request = new AccountRequest();
        request.setAccountNumber("ACC001");
        request.setBalance(new BigDecimal("1000.00"));
        request.setCustomerId(1L);
        request.setAccountType("SAVINGS");
        request.setCurrency("USD");

        given(accountRepository.existsByAccountNumber("ACC001")).willReturn(true);

        assertThatThrownBy(() -> accountService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Account number already exists");

        then(accountRepository).should(never()).save(any(Account.class));
    }

    @Test
    void shouldFindAllAccounts() {
        var accounts = List.of(
                accountWithId(1L, "ACC001", new BigDecimal("1000.00"), 1L, "SAVINGS", "USD"),
                accountWithId(2L, "ACC002", new BigDecimal("500.00"), 2L, "CHECKING", "EUR")
        );
        given(accountRepository.findAll()).willReturn(accounts);

        List<AccountResponse> responses = accountService.findAll();

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getAccountNumber()).isEqualTo("ACC001");
        assertThat(responses.get(1).getAccountNumber()).isEqualTo("ACC002");
    }

    @Test
    void shouldFindAccountById() {
        var account = accountWithId(1L, "ACC001", new BigDecimal("1000.00"), 1L, "SAVINGS", "USD");
        given(accountRepository.findById(1L)).willReturn(Optional.of(account));

        AccountResponse response = accountService.findById(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getAccountNumber()).isEqualTo("ACC001");
        assertThat(response.getBalance()).isEqualByComparingTo(new BigDecimal("1000.00"));
    }

    @Test
    void shouldThrowWhenAccountNotFound() {
        given(accountRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> accountService.findById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Account not found");
    }

    @Test
    void shouldUpdateAccount() {
        var existing = accountWithId(1L, "ACC001", new BigDecimal("1000.00"), 1L, "SAVINGS", "USD");
        var request = new AccountRequest();
        request.setAccountNumber("ACC001");
        request.setBalance(new BigDecimal("2000.00"));
        request.setCustomerId(1L);
        request.setAccountType("CHECKING");
        request.setCurrency("EUR");

        given(accountRepository.findById(1L)).willReturn(Optional.of(existing));
        given(accountRepository.save(any(Account.class))).willReturn(existing);

        AccountResponse response = accountService.update(1L, request);

        assertThat(response.getAccountType()).isEqualTo("CHECKING");
        assertThat(response.getCurrency()).isEqualTo("EUR");
        assertThat(response.getBalance()).isEqualByComparingTo(new BigDecimal("2000.00"));
    }

    @Test
    void shouldDeleteAccount() {
        given(accountRepository.existsById(1L)).willReturn(true);

        accountService.delete(1L);

        then(accountRepository).should().deleteById(1L);
    }

    @Test
    void shouldThrowWhenDeletingNonExistent() {
        given(accountRepository.existsById(99L)).willReturn(false);

        assertThatThrownBy(() -> accountService.delete(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Account not found");
    }

    @Test
    void shouldFindAccountsByCustomerId() {
        var accounts = List.of(
                accountWithId(1L, "ACC001", new BigDecimal("1000.00"), 1L, "SAVINGS", "USD"),
                accountWithId(2L, "ACC002", new BigDecimal("500.00"), 1L, "CHECKING", "USD")
        );
        given(accountRepository.findByCustomerId(1L)).willReturn(accounts);

        List<AccountResponse> responses = accountService.findByCustomerId(1L);

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getCustomerId()).isEqualTo(1L);
        assertThat(responses.get(1).getCustomerId()).isEqualTo(1L);
    }
}
