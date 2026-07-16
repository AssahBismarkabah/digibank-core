package com.digibank.compliance.model;

import com.digibank.shared.model.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "compliance_checks")
public class ComplianceCheck extends BaseEntity {

    public enum CheckType {
        KYC, AML, SANCTIONS_SCREENING, PEP_CHECK, CDD, TRANSACTION_MONITORING
    }

    public enum CheckStatus {
        PENDING, PASSED, FAILED, REVIEW_REQUIRED
    }

    @NotNull(message = "Customer ID is required")
    @Column(name = "customer_id", nullable = false)
    private Long customerId;

    @NotNull(message = "Check type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "check_type", nullable = false, length = 30)
    private CheckType checkType;

    @NotNull(message = "Check status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private CheckStatus status;

    @NotBlank(message = "Checked by is required")
    @Column(name = "checked_by", length = 100)
    private String checkedBy;

    @Column(columnDefinition = "TEXT")
    private String remarks;

    @NotNull(message = "Check date is required")
    @Column(name = "check_date", nullable = false)
    private LocalDateTime checkDate;

    protected ComplianceCheck() {
    }

    public ComplianceCheck(Long customerId, CheckType checkType, CheckStatus status,
                           String checkedBy, String remarks, LocalDateTime checkDate) {
        this.customerId = customerId;
        this.checkType = checkType;
        this.status = status;
        this.checkedBy = checkedBy;
        this.remarks = remarks;
        this.checkDate = checkDate;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(Long customerId) {
        this.customerId = customerId;
    }

    public CheckType getCheckType() {
        return checkType;
    }

    public void setCheckType(CheckType checkType) {
        this.checkType = checkType;
    }

    public CheckStatus getStatus() {
        return status;
    }

    public void setStatus(CheckStatus status) {
        this.status = status;
    }

    public String getCheckedBy() {
        return checkedBy;
    }

    public void setCheckedBy(String checkedBy) {
        this.checkedBy = checkedBy;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }

    public LocalDateTime getCheckDate() {
        return checkDate;
    }

    public void setCheckDate(LocalDateTime checkDate) {
        this.checkDate = checkDate;
    }
}
