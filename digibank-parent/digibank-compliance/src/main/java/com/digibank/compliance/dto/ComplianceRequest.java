package com.digibank.compliance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ComplianceRequest {

    @NotNull(message = "Customer ID is required")
    private Long customerId;

    @NotBlank(message = "Check type is required")
    private String checkType;

    @NotBlank(message = "Status is required")
    private String status;

    @NotBlank(message = "Checked by is required")
    private String checkedBy;

    private String remarks;

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
}
