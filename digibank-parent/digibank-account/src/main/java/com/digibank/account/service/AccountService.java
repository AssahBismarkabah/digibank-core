package com.digibank.account.service;

import com.digibank.account.dto.AccountRequest;
import com.digibank.account.dto.AccountResponse;
import com.digibank.account.dto.AccountSummaryResponse;
import com.digibank.account.model.Account;
import com.digibank.account.repository.AccountRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public AccountResponse create(AccountRequest request) {
        if (accountRepository.existsByAccountNumber(request.getAccountNumber())) {
            throw new IllegalArgumentException("Account number already exists: " + request.getAccountNumber());
        }
        var account = new Account(request.getAccountNumber(), request.getBalance(),
                request.getCustomerId(), request.getAccountType(), request.getCurrency());
        account = accountRepository.save(account);
        return toResponse(account);
    }

    @Transactional(readOnly = true)
    public List<AccountSummaryResponse> findAll() {
        return accountRepository.findAll().stream()
                .map(this::toSummaryResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public AccountResponse findById(Long id) {
        return accountRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Account not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<AccountSummaryResponse> findByCustomerId(Long customerId) {
        return accountRepository.findByCustomerId(customerId).stream()
                .map(this::toSummaryResponse)
                .toList();
    }

    public AccountResponse update(Long id, AccountRequest request) {
        var account = accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account not found with id: " + id));
        account.setAccountNumber(request.getAccountNumber());
        account.setBalance(request.getBalance());
        account.setCustomerId(request.getCustomerId());
        account.setAccountType(request.getAccountType());
        account.setCurrency(request.getCurrency());
        account = accountRepository.save(account);
        return toResponse(account);
    }

    public void delete(Long id) {
        if (!accountRepository.existsById(id)) {
            throw new EntityNotFoundException("Account not found with id: " + id);
        }
        accountRepository.deleteById(id);
    }

    private AccountResponse toResponse(Account account) {
        return new AccountResponse(account.getId(), maskAccountNumber(account.getAccountNumber()),
                account.getBalance(), account.getCustomerId(),
                account.getAccountType(), account.getCurrency());
    }

    private AccountSummaryResponse toSummaryResponse(Account account) {
        return new AccountSummaryResponse(account.getId(), maskAccountNumber(account.getAccountNumber()),
                account.getAccountType(), account.getCurrency());
    }

    private String maskAccountNumber(String accountNumber) {
        if (accountNumber == null || accountNumber.length() <= 4) {
            return "****";
        }
        return "****" + accountNumber.substring(accountNumber.length() - 4);
    }
}
