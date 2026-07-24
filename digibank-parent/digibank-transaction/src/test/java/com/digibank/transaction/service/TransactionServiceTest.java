package com.digibank.transaction.service;

import com.digibank.account.model.Account;
import com.digibank.account.repository.AccountRepository;
import com.digibank.transaction.dto.TransactionRequest;
import com.digibank.transaction.dto.TransactionResponse;
import com.digibank.transaction.model.Transaction;
import com.digibank.transaction.repository.TransactionRepository;
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
class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Account accountWithId(Long id, BigDecimal balance) {
        var account = new Account("ACC-001", balance, 1L, "CHECKING", "USD");
        account.setId(id);
        return account;
    }

    private Transaction transactionWithId(Long id, Long accountId, BigDecimal amount,
                                          String transactionType, String description) {
        var transaction = new Transaction(accountId, amount, transactionType, description);
        transaction.setId(id);
        return transaction;
    }

    @Test
    void shouldCreateDepositAndUpdateBalance() {
        var request = new TransactionRequest();
        request.setAccountId(1L);
        request.setAmount(new BigDecimal("50.00"));
        request.setTransactionType("DEPOSIT");
        request.setDescription("Salary");

        var account = accountWithId(1L, new BigDecimal("100.00"));
        var savedTransaction = transactionWithId(1L, 1L, new BigDecimal("50.00"), "DEPOSIT", "Salary");

        given(accountRepository.findById(1L)).willReturn(Optional.of(account));
        given(accountRepository.save(any(Account.class))).willAnswer(invocation -> invocation.getArgument(0));
        given(transactionRepository.save(any(Transaction.class))).willReturn(savedTransaction);

        TransactionResponse response = transactionService.create(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(account.getBalance()).isEqualByComparingTo("150.00");
        then(accountRepository).should().save(any(Account.class));
        then(transactionRepository).should().save(any(Transaction.class));
    }

    @Test
    void shouldRejectWithdrawalWhenBalanceIsInsufficient() {
        var request = new TransactionRequest();
        request.setAccountId(1L);
        request.setAmount(new BigDecimal("100.00"));
        request.setTransactionType("WITHDRAWAL");
        request.setDescription("Rent");

        var account = accountWithId(1L, new BigDecimal("50.00"));
        given(accountRepository.findById(1L)).willReturn(Optional.of(account));

        assertThatThrownBy(() -> transactionService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Insufficient balance");

        assertThat(account.getBalance()).isEqualByComparingTo("50.00");
        then(transactionRepository).should(never()).save(any(Transaction.class));
    }

    @Test
    void shouldFindTransactionById() {
        var transaction = transactionWithId(1L, 2L, new BigDecimal("25.00"), "DEPOSIT", "Refund");
        given(transactionRepository.findById(1L)).willReturn(Optional.of(transaction));

        TransactionResponse response = transactionService.findById(1L);

        assertThat(response.getAccountId()).isEqualTo(2L);
        assertThat(response.getDescription()).isEqualTo("Refund");
    }

    @Test
    void shouldFindAllTransactions() {
        var transactions = List.of(
                transactionWithId(1L, 1L, new BigDecimal("10.00"), "DEPOSIT", "Coffee"),
                transactionWithId(2L, 1L, new BigDecimal("20.00"), "WITHDRAWAL", "Dinner")
        );
        given(transactionRepository.findAll()).willReturn(transactions);

        List<TransactionResponse> responses = transactionService.findAll();

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getDescription()).isEqualTo("Coffee");
    }

    @Test
    void shouldUpdateTransaction() {
        var existing = transactionWithId(1L, 1L, new BigDecimal("25.00"), "DEPOSIT", "Old description");
        var request = new TransactionRequest();
        request.setAccountId(1L);
        request.setAmount(new BigDecimal("75.00"));
        request.setTransactionType("DEPOSIT");
        request.setDescription("Updated description");

        given(transactionRepository.findById(1L)).willReturn(Optional.of(existing));
        given(transactionRepository.save(any(Transaction.class))).willAnswer(invocation -> invocation.getArgument(0));

        TransactionResponse response = transactionService.update(1L, request);

        assertThat(response.getAmount()).isEqualByComparingTo("75.00");
        assertThat(response.getDescription()).isEqualTo("Updated description");
    }

    @Test
    void shouldDeleteTransaction() {
        given(transactionRepository.existsById(1L)).willReturn(true);

        transactionService.delete(1L);

        then(transactionRepository).should().deleteById(1L);
    }

    @Test
    void shouldFindTransactionsByAccountId() {
        var transactions = List.of(
                transactionWithId(1L, 10L, new BigDecimal("15.00"), "DEPOSIT", "Salary"),
                transactionWithId(2L, 10L, new BigDecimal("5.00"), "WITHDRAWAL", "Coffee")
        );
        given(transactionRepository.findByAccountIdOrderByTransactionDateDesc(10L)).willReturn(transactions);

        List<TransactionResponse> responses = transactionService.findByAccountId(10L);

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getAccountId()).isEqualTo(10L);
        assertThat(responses.get(0).getDescription()).isEqualTo("Salary");
    }
}
