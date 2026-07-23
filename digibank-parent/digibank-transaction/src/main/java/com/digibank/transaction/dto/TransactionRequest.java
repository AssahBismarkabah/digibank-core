package com.digibank.transaction.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class TransactionRequest {

    @NotNull(message = "Account ID is required")
    private Long accountId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    @Digits(integer = 12, fraction = 2, message = "Amount must have at most 12 integer and 2 fraction digits")
    private BigDecimal amount;

    @NotBlank(message = "Transaction type is required")
    private String transactionType;

    @NotBlank(message = "Description is required")
    private String description;

    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
