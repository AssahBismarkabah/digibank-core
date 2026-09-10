package com.digibank.compliance.dto;
import java.time.LocalDateTime;
public record ComplianceResponse(Long id, Long customerId, String checkType, String status, String remarks, LocalDateTime checkDate) { }
