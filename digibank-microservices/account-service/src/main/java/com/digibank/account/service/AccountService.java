package com.digibank.account.service;

import com.digibank.account.dto.*;
import com.digibank.account.model.Account;
import com.digibank.account.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service @Transactional
public class AccountService {
    private final AccountRepository repository;
    public AccountService(AccountRepository repository) { this.repository = repository; }
    public AccountResponse create(AccountRequest r) { if (repository.existsByAccountNumber(r.accountNumber())) throw new IllegalArgumentException("Account number already exists"); return response(repository.save(new Account(r.accountNumber(), r.balance(), r.customerId(), r.accountType(), r.currency()))); }
    @Transactional(readOnly = true) public List<AccountSummaryResponse> findAll() { return repository.findAll().stream().map(this::summary).toList(); }
    @Transactional(readOnly = true) public AccountResponse findById(Long id) { return response(repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Account not found"))); }
    @Transactional(readOnly = true) public List<AccountSummaryResponse> findByCustomerId(Long id) { return repository.findByCustomerId(id).stream().map(this::summary).toList(); }
    public AccountResponse update(Long id, AccountRequest r) { Account a = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Account not found")); a.update(r.accountNumber(), r.balance(), r.customerId(), r.accountType(), r.currency()); return response(a); }
    public void updateBalance(Long id, java.math.BigDecimal amount, String operation) { Account a = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Account not found")); if (amount == null || amount.signum() <= 0) throw new IllegalArgumentException("Amount must be positive"); if ("CREDIT".equals(operation)) a.update(a.getAccountNumber(), a.getBalance().add(amount), a.getCustomerId(), a.getAccountType(), a.getCurrency()); else if ("DEBIT".equals(operation) && a.getBalance().compareTo(amount) >= 0) a.update(a.getAccountNumber(), a.getBalance().subtract(amount), a.getCustomerId(), a.getAccountType(), a.getCurrency()); else throw new IllegalArgumentException("Transaction could not be processed"); }
    public void delete(Long id) { if (!repository.existsById(id)) throw new EntityNotFoundException("Account not found"); repository.deleteById(id); }
    private AccountResponse response(Account a) { return new AccountResponse(a.getId(), mask(a.getAccountNumber()), a.getBalance(), a.getCustomerId(), a.getAccountType(), a.getCurrency()); }
    private AccountSummaryResponse summary(Account a) { return new AccountSummaryResponse(a.getId(), mask(a.getAccountNumber()), a.getAccountType(), a.getCurrency()); }
    private String mask(String number) { return number == null || number.length() <= 4 ? "****" : "****" + number.substring(number.length() - 4); }
}
