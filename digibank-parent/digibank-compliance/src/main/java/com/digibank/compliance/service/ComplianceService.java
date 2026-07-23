package com.digibank.compliance.service;

import com.digibank.compliance.dto.ComplianceRequest;
import com.digibank.compliance.dto.ComplianceResponse;
import com.digibank.compliance.model.ComplianceCheck;
import com.digibank.compliance.repository.ComplianceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class ComplianceService {

    private final ComplianceRepository complianceRepository;

    public ComplianceService(ComplianceRepository complianceRepository) {
        this.complianceRepository = complianceRepository;
    }

    public ComplianceResponse create(ComplianceRequest request) {
        var check = new ComplianceCheck(request.getCustomerId(), request.getCheckType(),
                request.getStatus(), request.getCheckedBy(), request.getRemarks());
        check = complianceRepository.save(check);
        return toResponse(check);
    }

    @Transactional(readOnly = true)
    public List<ComplianceResponse> findAll() {
        return complianceRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ComplianceResponse findById(Long id) {
        return complianceRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Compliance check not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public List<ComplianceResponse> findByCustomerId(Long customerId) {
        return complianceRepository.findByCustomerId(customerId).stream()
                .map(this::toResponse)
                .toList();
    }

    public ComplianceResponse update(Long id, ComplianceRequest request) {
        var check = complianceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Compliance check not found with id: " + id));
        check.setCustomerId(request.getCustomerId());
        check.setCheckType(request.getCheckType());
        check.setStatus(request.getStatus());
        check.setCheckedBy(request.getCheckedBy());
        check.setRemarks(request.getRemarks());
        check = complianceRepository.save(check);
        return toResponse(check);
    }

    public void delete(Long id) {
        if (!complianceRepository.existsById(id)) {
            throw new EntityNotFoundException("Compliance check not found with id: " + id);
        }
        complianceRepository.deleteById(id);
    }

    private ComplianceResponse toResponse(ComplianceCheck check) {
        return new ComplianceResponse(check.getId(), check.getCustomerId(),
                check.getCheckType(), check.getStatus(), check.getCheckedBy(),
                check.getRemarks(), check.getCheckDate());
    }
}
