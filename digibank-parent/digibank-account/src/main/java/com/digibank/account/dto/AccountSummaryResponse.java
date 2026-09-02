package com.digibank.account.dto;

public class AccountSummaryResponse {

    private Long id;
    private String maskedAccountNumber;
    private String accountType;
    private String currency;

    public AccountSummaryResponse() {}

    public AccountSummaryResponse(Long id, String maskedAccountNumber,
                                  String accountType, String currency) {
        this.id = id;
        this.maskedAccountNumber = maskedAccountNumber;
        this.accountType = accountType;
        this.currency = currency;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getMaskedAccountNumber() { return maskedAccountNumber; }
    public void setMaskedAccountNumber(String maskedAccountNumber) { this.maskedAccountNumber = maskedAccountNumber; }
    public String getAccountType() { return accountType; }
    public void setAccountType(String accountType) { this.accountType = accountType; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
}
