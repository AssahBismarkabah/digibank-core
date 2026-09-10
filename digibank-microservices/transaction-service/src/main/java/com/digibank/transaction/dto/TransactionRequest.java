package com.digibank.transaction.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
public record TransactionRequest(@NotNull Long accountId, @NotNull @Positive @Digits(integer = 12, fraction = 2) BigDecimal amount, @NotBlank String transactionType, @NotBlank String description) { }
