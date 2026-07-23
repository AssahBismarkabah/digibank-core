package com.digibank.transaction.model;

import com.digibank.shared.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
public class Transaction extends BaseEntity {

    @Column(name = "account_id", nullable = false)
    private Long accountId;

    @NotNull
    @Positive
    @Digits(integer = 12, fraction = 2)
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal amount;

    @NotBlank
    @Column(name = "transaction_type", nullable = false, length = 20)
    private String transactionType;

    @NotBlank
    @Column(length = 500)
    private String description;

    @Column(name = "reference_number", unique = true, length = 36, nullable = false)
    private String referenceNumber;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    public Transaction() {}

    public Transaction(Long accountId, BigDecimal amount, String transactionType, String description) {
        this.accountId = accountId;
        this.amount = amount;
        this.transactionType = transactionType;
        this.description = description;
        this.referenceNumber = UUID.randomUUID().toString();
        this.transactionDate = LocalDateTime.now();
    }

    public Long getAccountId() { return accountId; }
    public void setAccountId(Long accountId) { this.accountId = accountId; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public String getTransactionType() { return transactionType; }
    public void setTransactionType(String transactionType) { this.transactionType = transactionType; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getReferenceNumber() { return referenceNumber; }
    public LocalDateTime getTransactionDate() { return transactionDate; }
}
