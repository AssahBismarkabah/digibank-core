package com.digibank.transaction.service;

import com.digibank.account.model.Account;
import com.digibank.account.repository.AccountRepository;
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

    public TransactionService(TransactionRepository transactionRepository,
                              AccountRepository accountRepository) {
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
    }

    public TransactionResponse create(TransactionRequest request) {
        var account = accountRepository.findById(request.getAccountId())
                .orElseThrow(() -> new EntityNotFoundException("Account not found with id: " + request.getAccountId()));

        var transaction = new Transaction(request.getAccountId(), request.getAmount(),
                request.getTransactionType(), request.getDescription());

        // Update account balance
        var newBalance = switch (request.getTransactionType().toUpperCase()) {
            case "DEPOSIT" -> account.getBalance().add(request.getAmount());
            case "WITHDRAWAL", "TRANSFER", "PAYMENT" -> {
                if (account.getBalance().compareTo(request.getAmount()) < 0) {
                    throw new IllegalArgumentException("Insufficient balance");
                }
                yield account.getBalance().subtract(request.getAmount());
            }
            default -> throw new IllegalArgumentException("Unknown transaction type: " + request.getTransactionType());
        };
        account.setBalance(newBalance);
        accountRepository.save(account);

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

    private TransactionResponse toResponse(Transaction transaction) {
        return new TransactionResponse(transaction.getId(), transaction.getAccountId(),
                transaction.getAmount(), transaction.getTransactionType(),
                transaction.getDescription(), transaction.getReferenceNumber(),
                transaction.getTransactionDate());
    }
}
