package com.digibank.transaction.controller;
import com.digibank.transaction.dto.*;
import com.digibank.transaction.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/transactions") public class TransactionController {
    private final TransactionService service;
    private final com.digibank.transaction.service.TransactionQueryService queryService;
    public TransactionController(TransactionService service, com.digibank.transaction.service.TransactionQueryService queryService) { this.service = service; this.queryService = queryService; }
    @GetMapping public List<TransactionResponse> findAll() { return queryService.findAll(); }
    @GetMapping("/{id}") public TransactionResponse findById(@PathVariable Long id) { return service.findById(id); }
    @GetMapping("/by-account/{accountId}") public List<TransactionResponse> findByAccountId(@PathVariable Long accountId) { return queryService.findByAccountId(accountId); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public TransactionResponse create(@Valid @RequestBody TransactionRequest request) { return service.create(request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id) { service.delete(id); }
}
