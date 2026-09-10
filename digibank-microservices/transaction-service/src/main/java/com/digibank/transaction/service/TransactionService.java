package com.digibank.transaction.service;

import com.digibank.transaction.client.AccountClient;
import com.digibank.transaction.client.NotificationClient;
import com.digibank.transaction.dto.TransactionRequest;
import com.digibank.transaction.dto.TransactionResponse;
import com.digibank.transaction.model.Transaction;
import com.digibank.transaction.repository.TransactionRepository;
import com.digibank.transaction.pattern.TransferSaga;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service @Transactional
public class TransactionService {
    private final TransactionRepository repository;
    private final AccountClient accountClient;
    private final NotificationClient notificationClient;
    private final TransferSaga transferSaga = new TransferSaga();
    public TransactionService(TransactionRepository repository, AccountClient accountClient, NotificationClient notificationClient) { this.repository = repository; this.accountClient = accountClient; this.notificationClient = notificationClient; }
    public TransactionResponse create(TransactionRequest request) {
        String type = request.transactionType().trim().toUpperCase();
        if (!List.of("DEPOSIT", "WITHDRAWAL", "TRANSFER", "PAYMENT").contains(type)) throw new IllegalArgumentException("Unsupported transaction type");
        String operation = type.equals("DEPOSIT") ? "CREDIT" : "DEBIT";
        Transaction transaction = new Transaction(request.accountId(), request.amount(), type, request.description());
        Transaction completed = transferSaga.execute(transaction,
                () -> accountClient.updateBalance(request.accountId(), request.amount(), operation),
                () -> repository.save(transaction),
                () -> notificationClient.notify(transaction.getReferenceNumber()),
                (saved, failure) -> accountClient.compensateBalance(request.accountId(), request.amount(), operation));
        return response(completed);
    }
    @Transactional(readOnly = true) public List<TransactionResponse> findAll() { return repository.findAll().stream().map(this::response).toList(); }
    @Transactional(readOnly = true) public TransactionResponse findById(Long id) { return response(repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Transaction not found"))); }
    @Transactional(readOnly = true) public List<TransactionResponse> findByAccountId(Long id) { return repository.findByAccountIdOrderByTransactionDateDesc(id).stream().map(this::response).toList(); }
    public void delete(Long id) { if (!repository.existsById(id)) throw new EntityNotFoundException("Transaction not found"); repository.deleteById(id); }
    private TransactionResponse response(Transaction t) { return new TransactionResponse(t.getId(), t.getAccountId(), t.getAmount(), t.getTransactionType(), t.getDescription(), t.getReferenceNumber(), t.getTransactionDate()); }
}
