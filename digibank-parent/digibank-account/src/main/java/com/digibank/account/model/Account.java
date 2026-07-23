package com.digibank.account.model;

import com.digibank.shared.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

@Entity
@Table(name = "accounts")
public class Account extends BaseEntity {

    @NotBlank
    @Column(name = "account_number", unique = true, nullable = false, length = 20)
    private String accountNumber;

    @NotNull
    @PositiveOrZero
    @Digits(integer = 12, fraction = 2)
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal balance;

    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @NotBlank
    @Column(name = "account_type", nullable = false, length = 20)
    private String accountType;

    @NotBlank
    @Column(length = 3)
    private String currency;

    public Account() {}

    public Account(String accountNumber, BigDecimal balance, Long customerId,
                   String accountType, String currency) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.customerId = customerId;
        this.accountType = accountType;
        this.currency = currency;
    }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }
    public BigDecimal getBalance() { return balance; }
    public void setBalance(BigDecimal balance) { this.balance = balance; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}
