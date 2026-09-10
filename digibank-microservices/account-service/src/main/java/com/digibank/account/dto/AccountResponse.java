package com.digibank.account.dto;

import java.math.BigDecimal;
public record AccountResponse(Long id, String maskedAccountNumber, BigDecimal balance, Long customerId, String accountType, String currency) { }
