package com.digibank.account.dto;

import java.math.BigDecimal;

public class AccountResponse {

    private Long id;
    private String accountNumber;
    private BigDecimal balance;
    private Long customerId;
    private String accountType;
    private String currency;

    public AccountResponse() {
    }

    public AccountResponse(Long id, String accountNumber, BigDecimal balance, Long customerId,
                           String accountType, String currency) {
        this.id = id;
        this.accountNumber = accountNumber;
        this.balance = balance;
        this.customerId = customerId;
        this.accountType = accountType;
        this.currency = currency;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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
