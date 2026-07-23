package com.digibank.compliance.model;

import com.digibank.shared.model.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "compliance_checks")
public class ComplianceCheck extends BaseEntity {

    @NotNull
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @NotBlank
    @Column(name = "check_type", nullable = false, length = 30)
    private String checkType;

    @NotBlank
    @Column(nullable = false, length = 20)
    private String status;

    @NotBlank
    @Column(name = "checked_by", nullable = false, length = 50)
    private String checkedBy;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @Column(name = "check_date", nullable = false)
    private LocalDateTime checkDate;

    public ComplianceCheck() {}

    public ComplianceCheck(Long customerId, String checkType, String status,
                          String checkedBy, String remarks) {
        this.customerId = customerId;
        this.checkType = checkType;
        this.status = status;
        this.checkedBy = checkedBy;
        this.remarks = remarks;
        this.checkDate = LocalDateTime.now();
    }

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
