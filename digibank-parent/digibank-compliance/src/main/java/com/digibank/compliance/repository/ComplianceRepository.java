package com.digibank.compliance.repository;

import com.digibank.compliance.model.ComplianceCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComplianceRepository extends JpaRepository<ComplianceCheck, Long> {
    List<ComplianceCheck> findByCustomerId(Long customerId);
    List<ComplianceCheck> findByStatus(String status);
}
