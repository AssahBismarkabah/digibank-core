package com.digibank.compliance.dto;

import java.time.LocalDateTime;

public class ComplianceResponse {

    private Long id;
    private Long customerId;
    private String checkType;
    private String status;
    private String checkedBy;
    private String remarks;
    private LocalDateTime checkDate;

    public ComplianceResponse() {}

    public ComplianceResponse(Long id, Long customerId, String checkType,
                             String status, String checkedBy, String remarks,
                             LocalDateTime checkDate) {
        this.id = id;
        this.customerId = customerId;
        this.checkType = checkType;
        this.status = status;
        this.checkedBy = checkedBy;
        this.remarks = remarks;
        this.checkDate = checkDate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCustomerId() { return customerId; }
    public void setCustomerId(Long customerId) { this.customerId = customerId; }
    public String getCheckType() { return checkType; }
    public void setCheckType(String checkType) { this.checkType = checkType; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCheckedBy() { return checkedBy; }
    public void setCheckedBy(String checkedBy) { this.checkedBy = checkedBy; }
    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }
    public LocalDateTime getCheckDate() { return checkDate; }
    public void setCheckDate(LocalDateTime checkDate) { this.checkDate = checkDate; }
}
