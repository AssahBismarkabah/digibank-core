package com.digibank.compliance.service;
import com.digibank.compliance.dto.*;
import com.digibank.compliance.model.ComplianceCheck;
import com.digibank.compliance.repository.ComplianceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
@Service @Transactional public class ComplianceService {
    private final ComplianceRepository repository;
    public ComplianceService(ComplianceRepository repository) { this.repository = repository; }
    public ComplianceResponse create(ComplianceRequest r) { return response(repository.save(new ComplianceCheck(r.customerId(), r.checkType(), r.status(), r.checkedBy(), r.remarks()))); }
    @Transactional(readOnly = true) public List<ComplianceSummaryResponse> findAll() { return repository.findAll().stream().map(this::summary).toList(); }
    @Transactional(readOnly = true) public ComplianceResponse findById(Long id) { return response(repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Compliance check not found"))); }
    @Transactional(readOnly = true) public List<ComplianceSummaryResponse> findByCustomerId(Long id) { return repository.findByCustomerId(id).stream().map(this::summary).toList(); }
    public ComplianceResponse update(Long id, ComplianceRequest r) { ComplianceCheck c = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Compliance check not found")); c.update(r.customerId(), r.checkType(), r.status(), r.checkedBy(), r.remarks()); return response(c); }
    public void delete(Long id) { if (!repository.existsById(id)) throw new EntityNotFoundException("Compliance check not found"); repository.deleteById(id); }
    private ComplianceResponse response(ComplianceCheck c) { return new ComplianceResponse(c.getId(), c.getCustomerId(), c.getCheckType(), c.getStatus(), c.getRemarks(), c.getCheckDate()); }
    private ComplianceSummaryResponse summary(ComplianceCheck c) { return new ComplianceSummaryResponse(c.getId(), c.getCheckType(), c.getStatus(), c.getCheckDate()); }
}
