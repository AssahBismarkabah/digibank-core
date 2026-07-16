package com.digibank.account.model;

import com.digibank.shared.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Table(name = "accounts")
public class Account extends BaseEntity {

    @NotBlank(message = "Account number is required")
    @Column(name = "account_number", unique = true, nullable = false, length = 20)
    private String accountNumber;

    @NotNull(message = "Balance is required")
    @PositiveOrZero(message = "Balance must be zero or positive")
    @Digits(integer = 12, fraction = 2, message = "Balance must have at most 12 integer and 2 fraction digits")
    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal balance;

    @NotNull(message = "Customer ID is required")
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @NotBlank(message = "Account type is required")
    @Column(name = "account_type", nullable = false, length = 20)
    private String accountType;

    @Column(length = 3)
    private String currency;

    protected Account() {
    }

    public Account(String accountNumber, BigDecimal balance, Long customerId, String accountType, String currency) {
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.customerId = customerId;
        this.accountType = accountType;
        this.currency = currency;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }
}
