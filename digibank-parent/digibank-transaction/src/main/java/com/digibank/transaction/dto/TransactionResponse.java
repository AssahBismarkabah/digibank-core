package com.digibank.transaction.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class TransactionResponse {

    private Long id;
    private Long accountId;
    private BigDecimal amount;
    private String transactionType;
    private String description;
    private LocalDateTime transactionDate;
    private String referenceNumber;

    public TransactionResponse() {
    }

    public TransactionResponse(Long id, Long accountId, BigDecimal amount, String transactionType,
                               String description, LocalDateTime transactionDate, String referenceNumber) {
        this.id = id;
        this.accountId = accountId;
        this.amount = amount;
        this.transactionType = transactionType;
        this.description = description;
        this.transactionDate = transactionDate;
        this.referenceNumber = referenceNumber;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getAccountId() {
        return accountId;
    }

    public void setAccountId(Long accountId) {
        this.accountId = accountId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(String transactionType) {
        this.transactionType = transactionType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transactionDate) {
        this.transactionDate = transactionDate;
    }

    public String getReferenceNumber() {
        return referenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.referenceNumber = referenceNumber;
    }
}
