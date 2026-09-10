package com.digibank.transaction.service;

import com.digibank.transaction.dto.TransactionResponse;
import com.digibank.transaction.model.Transaction;
import com.digibank.transaction.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class TransactionQueryService {
    private final TransactionRepository repository;
    public TransactionQueryService(TransactionRepository repository) { this.repository = repository; }
    @Transactional(readOnly = true)
    public List<TransactionResponse> findAll() { return repository.findAll().stream().map(this::response).toList(); }
    @Transactional(readOnly = true)
    public List<TransactionResponse> findByAccountId(Long id) { return repository.findByAccountIdOrderByTransactionDateDesc(id).stream().map(this::response).toList(); }
    private TransactionResponse response(Transaction t) { return new TransactionResponse(t.getId(), t.getAccountId(), t.getAmount(), t.getTransactionType(), t.getDescription(), t.getReferenceNumber(), t.getTransactionDate()); }
}
