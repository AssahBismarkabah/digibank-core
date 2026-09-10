package com.digibank.account.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public record AccountRequest(@NotBlank String accountNumber, @NotNull @PositiveOrZero @Digits(integer = 12, fraction = 2) BigDecimal balance, @NotNull Long customerId, @NotBlank String accountType, @NotBlank @Size(min = 3, max = 3) String currency) { }
