package com.digibank.compliance.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity @Table(name = "compliance_checks")
public class ComplianceCheck {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(name = "customer_id", nullable = false) private Long customerId;
    @Column(name = "check_type", nullable = false, length = 30) private String checkType;
    @Column(nullable = false, length = 20) private String status;
    @Column(name = "checked_by", nullable = false, length = 50) private String checkedBy;
    @Column(columnDefinition = "TEXT") private String remarks;
    @Column(name = "check_date", nullable = false) private LocalDateTime checkDate;
    protected ComplianceCheck() { }
    public ComplianceCheck(Long customerId, String type, String status, String checkedBy, String remarks) { this.customerId = customerId; this.checkType = type; this.status = status; this.checkedBy = checkedBy; this.remarks = remarks; this.checkDate = LocalDateTime.now(); }
    public Long getId() { return id; } public Long getCustomerId() { return customerId; } public String getCheckType() { return checkType; } public String getStatus() { return status; } public String getCheckedBy() { return checkedBy; } public String getRemarks() { return remarks; } public LocalDateTime getCheckDate() { return checkDate; }
    public void update(Long customerId, String type, String status, String checkedBy, String remarks) { this.customerId = customerId; this.checkType = type; this.status = status; this.checkedBy = checkedBy; this.remarks = remarks; }
}
