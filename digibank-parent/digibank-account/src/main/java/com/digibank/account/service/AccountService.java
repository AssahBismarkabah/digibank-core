package com.digibank.account.service;

import com.digibank.account.dto.AccountRequest;
import com.digibank.account.dto.AccountResponse;
import com.digibank.account.model.Account;
import com.digibank.account.repository.AccountRepository;
import com.digibank.shared.exception.EntityNotFoundException;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Stateless
public class AccountService {

    @Inject
    private AccountRepository accountRepository;

    public AccountResponse createAccount(AccountRequest request) {
        Account account = new Account(
                request.getAccountNumber(),
                request.getBalance(),
                request.getCustomerId(),
                request.getAccountType(),
                request.getCurrency() != null ? request.getCurrency() : "EUR"
        );
        Account saved = accountRepository.save(account);
        return toResponse(saved);
    }

    public Optional<AccountResponse> getAccountById(Long id) {
        return accountRepository.findById(id).map(this::toResponse);
    }

    public List<AccountResponse> getAllAccounts() {
        return accountRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<AccountResponse> getAccountsByCustomerId(Long customerId) {
        return accountRepository.findByCustomerId(customerId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public AccountResponse updateAccount(Long id, AccountRequest request) {
        Account account = accountRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Account", id));
        account.setAccountNumber(request.getAccountNumber());
        account.setBalance(request.getBalance());
        account.setCustomerId(request.getCustomerId());
        account.setAccountType(request.getAccountType());
        account.setCurrency(request.getCurrency() != null ? request.getCurrency() : "EUR");
        Account updated = accountRepository.save(account);
        return toResponse(updated);
    }

    public void deleteAccount(Long id) {
        accountRepository.findById(id).ifPresentOrElse(
                accountRepository::delete,
                () -> { throw new EntityNotFoundException("Account", id); }
        );
    }

    private AccountResponse toResponse(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getBalance(),
                account.getCustomerId(),
                account.getAccountType(),
                account.getCurrency()
        );
    }
}
