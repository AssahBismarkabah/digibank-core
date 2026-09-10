package com.digibank.transaction.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity @Table(name = "transactions")
public class Transaction {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "account_id", nullable = false) private Long accountId;
    @Column(nullable = false, precision = 14, scale = 2) private BigDecimal amount;
    @Column(name = "transaction_type", nullable = false, length = 20) private String transactionType;
    @Column(length = 500, nullable = false) private String description;
    @Column(name = "reference_number", unique = true, nullable = false, length = 36) private String referenceNumber;
    @Column(name = "transaction_date", nullable = false) private LocalDateTime transactionDate;
    protected Transaction() { }
    public Transaction(Long accountId, BigDecimal amount, String type, String description) { this.accountId = accountId; this.amount = amount; this.transactionType = type; this.description = description; this.referenceNumber = UUID.randomUUID().toString(); this.transactionDate = LocalDateTime.now(); }
    public Long getId() { return id; } public Long getAccountId() { return accountId; } public BigDecimal getAmount() { return amount; } public String getTransactionType() { return transactionType; } public String getDescription() { return description; } public String getReferenceNumber() { return referenceNumber; } public LocalDateTime getTransactionDate() { return transactionDate; }
}
