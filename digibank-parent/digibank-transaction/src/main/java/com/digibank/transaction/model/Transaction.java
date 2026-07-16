package com.digibank.transaction.model;

import com.digibank.shared.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "transactions")
public class Transaction extends BaseEntity {

    public enum TransactionType {
        DEPOSIT, WITHDRAWAL, TRANSFER, PAYMENT
    }

    @NotNull(message = "Account ID is required")
    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @NotNull(message = "Amount is required")
    @Positive(message = "Amount must be positive")
    @Digits(integer = 12, fraction = 2, message = "Amount must have at most 12 integer and 2 fraction digits")
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    @NotNull(message = "Transaction type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 20)
    private TransactionType transactionType;

    @NotBlank(message = "Description is required")
    @Column(length = 255)
    private String description;

    @NotNull(message = "Transaction date is required")
    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @Column(name = "reference_number", unique = true, length = 36)
    private String referenceNumber;

    protected Transaction() {
    }

    public Transaction(Long accountId, BigDecimal amount, TransactionType transactionType,
                       String description, LocalDateTime transactionDate, String referenceNumber) {
        this.accountId = accountId;
        this.amount = amount;
        this.transactionType = transactionType;
        this.description = description;
        this.transactionDate = transactionDate;
        this.referenceNumber = referenceNumber;
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

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
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
