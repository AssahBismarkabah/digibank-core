package com.digibank.account.model;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
public class Account {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "account_number", unique = true, nullable = false, length = 20) private String accountNumber;
    @Column(nullable = false, precision = 14, scale = 2) private BigDecimal balance;
    @Column(name = "customer_id", nullable = false) private Long customerId;
    @Column(name = "account_type", nullable = false, length = 20) private String accountType;
    @Column(nullable = false, length = 3) private String currency;
    protected Account() { }
    public Account(String number, BigDecimal balance, Long customerId, String type, String currency) { this.accountNumber = number; this.balance = balance; this.customerId = customerId; this.accountType = type; this.currency = currency; }
    public Long getId() { return id; } public String getAccountNumber() { return accountNumber; } public BigDecimal getBalance() { return balance; } public Long getCustomerId() { return customerId; } public String getAccountType() { return accountType; } public String getCurrency() { return currency; }
    public void update(String number, BigDecimal balance, Long customerId, String type, String currency) { this.accountNumber = number; this.balance = balance; this.customerId = customerId; this.accountType = type; this.currency = currency; }
}
