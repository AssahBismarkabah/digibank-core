package com.digibank.compliance.dto;
import java.time.LocalDateTime;
public record ComplianceSummaryResponse(Long id, String checkType, String status, LocalDateTime checkDate) { }
