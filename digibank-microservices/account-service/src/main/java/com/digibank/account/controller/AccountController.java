package com.digibank.account.controller;

import com.digibank.account.dto.*;
import com.digibank.account.service.AccountService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController @RequestMapping("/api/accounts")
public class AccountController {
    private final AccountService service;
    public AccountController(AccountService service) { this.service = service; }
    @GetMapping public List<AccountSummaryResponse> findAll() { return service.findAll(); }
    @GetMapping("/{id}") public AccountResponse findById(@PathVariable Long id) { return service.findById(id); }
    @GetMapping("/by-customer/{customerId}") public List<AccountSummaryResponse> findByCustomerId(@PathVariable Long customerId) { return service.findByCustomerId(customerId); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public AccountResponse create(@Valid @RequestBody AccountRequest request) { return service.create(request); }
    @PutMapping("/{id}") public AccountResponse update(@PathVariable Long id, @Valid @RequestBody AccountRequest request) { return service.update(id, request); }
    @PostMapping("/internal/{id}/balance") public void updateBalance(@PathVariable Long id, @RequestBody BalanceRequest request) { service.updateBalance(id, request.amount(), request.operation()); }
    @PostMapping("/internal/{id}/balance/compensate") public void compensateBalance(@PathVariable Long id, @RequestBody BalanceRequest request) { service.updateBalance(id, request.amount(), "CREDIT".equals(request.operation()) ? "DEBIT" : "CREDIT"); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id) { service.delete(id); }
    public record BalanceRequest(java.math.BigDecimal amount, String operation) { }
}
