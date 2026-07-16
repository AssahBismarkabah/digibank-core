package com.digibank.account.dto;

import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public class AccountRequest {

    @NotBlank(message = "Account number is required")
    private String accountNumber;

    @NotNull(message = "Initial balance is required")
    @PositiveOrZero(message = "Balance must be zero or positive")
    @Digits(integer = 12, fraction = 2, message = "Balance must have at most 12 integer and 2 fraction digits")
    private BigDecimal balance;

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotBlank(message = "Account type is required")
    private String accountType;

    private String currency;

    public AccountRequest() {
    }

    public AccountRequest(String accountNumber, BigDecimal balance, Long customerId, String accountType, String currency) {
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
