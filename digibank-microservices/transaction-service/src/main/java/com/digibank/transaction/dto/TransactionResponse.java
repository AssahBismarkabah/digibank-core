package com.digibank.transaction.dto;
import java.math.BigDecimal;
import java.time.LocalDateTime;
public record TransactionResponse(Long id, Long accountId, BigDecimal amount, String transactionType, String description, String referenceNumber, LocalDateTime transactionDate) { }
