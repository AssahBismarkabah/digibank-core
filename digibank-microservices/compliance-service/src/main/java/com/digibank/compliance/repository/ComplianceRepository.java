package com.digibank.compliance.repository;
import com.digibank.compliance.model.ComplianceCheck;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
public interface ComplianceRepository extends JpaRepository<ComplianceCheck, Long> { List<ComplianceCheck> findByCustomerId(Long customerId); }
