package com.digibank.transaction.service;

import com.digibank.shared.exception.EntityNotFoundException;
import com.digibank.transaction.dto.TransactionRequest;
import com.digibank.transaction.dto.TransactionResponse;
import com.digibank.transaction.model.Transaction;
import com.digibank.transaction.model.Transaction.TransactionType;
import com.digibank.transaction.repository.TransactionRepository;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Stateless
public class TransactionService {

    @Inject
    private TransactionRepository transactionRepository;

    public TransactionResponse createTransaction(TransactionRequest request) {
        Transaction transaction = new Transaction(
                request.getAccountId(),
                request.getAmount(),
                TransactionType.valueOf(request.getTransactionType()),
                request.getDescription(),
                LocalDateTime.now(),
                UUID.randomUUID().toString()
        );
        Transaction saved = transactionRepository.save(transaction);
        return toResponse(saved);
    }

    public Optional<TransactionResponse> getTransactionById(Long id) {
        return transactionRepository.findById(id).map(this::toResponse);
    }

    public List<TransactionResponse> getAllTransactions() {
        return transactionRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<TransactionResponse> getTransactionsByAccountId(Long accountId) {
        return transactionRepository.findByAccountId(accountId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public void deleteTransaction(Long id) {
        transactionRepository.findById(id).ifPresentOrElse(
                transactionRepository::delete,
                () -> { throw new EntityNotFoundException("Transaction", id); }
        );
    }

    private TransactionResponse toResponse(Transaction transaction) {
        return new TransactionResponse(
                transaction.getId(),
                transaction.getAccountId(),
                transaction.getAmount(),
                transaction.getTransactionType().name(),
                transaction.getDescription(),
                transaction.getTransactionDate(),
                transaction.getReferenceNumber()
        );
    }
}
