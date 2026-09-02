package com.digibank.compliance.dto;

import java.time.LocalDateTime;

public class ComplianceSummaryResponse {

    private Long id;
    private String checkType;
    private String status;
    private LocalDateTime checkDate;

    public ComplianceSummaryResponse() {}

    public ComplianceSummaryResponse(Long id, String checkType, String status,
                                     LocalDateTime checkDate) {
        this.id = id;
        this.checkType = checkType;
        this.status = status;
        this.checkDate = checkDate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getCheckType() { return checkType; }
    public void setCheckType(String checkType) { this.checkType = checkType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public LocalDateTime getCheckDate() { return checkDate; }
    public void setCheckDate(LocalDateTime checkDate) { this.checkDate = checkDate; }
}
