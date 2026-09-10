package com.digibank.compliance.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
public record ComplianceRequest(@NotNull Long customerId, @NotBlank String checkType, @NotBlank String status, @NotBlank String checkedBy, String remarks) { }
