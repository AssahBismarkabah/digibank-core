package com.digibank.transaction.service;

import com.digibank.account.model.Account;
import com.digibank.account.repository.AccountRepository;
import com.digibank.account.service.AccountService;
import com.digibank.transaction.dto.TransactionRequest;
import com.digibank.transaction.dto.TransactionResponse;
import com.digibank.transaction.model.Transaction;
import com.digibank.transaction.repository.TransactionRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@Transactional
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final AccountService accountService;

    public TransactionService(TransactionRepository transactionRepository,
                              AccountRepository accountRepository,
                              AccountService accountService) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.accountService = accountService;
    }

    public TransactionResponse create(TransactionRequest request) {
        validateRequest(request);
        var transactionType = normalizeTransactionType(request.getTransactionType());
        requireSupportedTransactionType(transactionType);

        var account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new EntityNotFoundException("Account not found with id: " + request.getAccountId()));

        applyBalanceUpdate(account, request.getAmount(), transactionType);

        var transaction = new Transaction(request.getAccountId(), request.getAmount(),
                transactionType, request.getDescription());
        transaction = transactionRepository.save(transaction);
        return toResponse(transaction);
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> findAll() {
        return transactionRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public TransactionResponse findById(Long id) {
        return transactionRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<TransactionResponse> findByAccountId(Long accountId) {
        return transactionRepository.findByAccountIdOrderByTransactionDateDesc(accountId).stream()
                .map(this::toResponse)
                .toList();
    }

    public TransactionResponse update(Long id, TransactionRequest request) {
        var transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Transaction not found with id: " + id));

        transaction.setAmount(request.getAmount());
        transaction.setTransactionType(request.getTransactionType());
        transaction.setDescription(request.getDescription());

        transaction = transactionRepository.save(transaction);
        return toResponse(transaction);
    }

    public void delete(Long id) {
        if (!transactionRepository.existsById(id)) {
            throw new EntityNotFoundException("Transaction not found with id: " + id);
        }
        transactionRepository.deleteById(id);
    }

    private void validateRequest(TransactionRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("Transaction request is required");
        }
        if (request.getAccountId() == null) {
            throw new IllegalArgumentException("Account ID is required");
        }
        if (request.getAmount() == null || request.getAmount().signum() <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (request.getTransactionType() == null || request.getTransactionType().isBlank()) {
            throw new IllegalArgumentException("Transaction type is required");
        }
    }

    private String normalizeTransactionType(String transactionType) {
        return transactionType.trim().toUpperCase();
    }

    private void requireSupportedTransactionType(String transactionType) {
        switch (transactionType) {
            case "DEPOSIT", "WITHDRAWAL", "TRANSFER", "PAYMENT" -> {
            }
            default -> throw new IllegalArgumentException("Unsupported transaction type");
        }
    }

    private void applyBalanceUpdate(Account account, BigDecimal amount, String transactionType) {
        switch (transactionType) {
            case "DEPOSIT" -> accountService.creditAccount(account, amount);
            case "WITHDRAWAL", "TRANSFER", "PAYMENT" -> accountService.debitAccount(account, amount);
            default -> throw new IllegalArgumentException("Unsupported transaction type");
        }
    }

    private TransactionResponse toResponse(Transaction transaction) {
        return new TransactionResponse(transaction.getId(), transaction.getAccountId(),
                transaction.getAmount(), transaction.getTransactionType(),
                transaction.getDescription(), transaction.getReferenceNumber(),
                transaction.getTransactionDate());
    }
}
