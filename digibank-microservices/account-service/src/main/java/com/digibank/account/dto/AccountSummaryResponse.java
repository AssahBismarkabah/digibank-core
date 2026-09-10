package com.digibank.account.dto;
public record AccountSummaryResponse(Long id, String maskedAccountNumber, String accountType, String currency) { }
