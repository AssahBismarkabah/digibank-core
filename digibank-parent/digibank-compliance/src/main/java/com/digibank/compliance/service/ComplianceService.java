package com.digibank.compliance.service;

import com.digibank.compliance.dto.ComplianceRequest;
import com.digibank.compliance.dto.ComplianceResponse;
import com.digibank.compliance.model.ComplianceCheck;
import com.digibank.compliance.model.ComplianceCheck.CheckStatus;
import com.digibank.compliance.model.ComplianceCheck.CheckType;
import com.digibank.compliance.repository.ComplianceRepository;
import com.digibank.shared.exception.EntityNotFoundException;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Stateless
public class ComplianceService {

    @Inject
    private ComplianceRepository complianceRepository;

    public ComplianceResponse createComplianceCheck(ComplianceRequest request) {
        ComplianceCheck check = new ComplianceCheck(
                request.getCustomerId(),
                CheckType.valueOf(request.getCheckType()),
                CheckStatus.valueOf(request.getStatus()),
                request.getCheckedBy(),
                request.getRemarks(),
                LocalDateTime.now()
        );
        ComplianceCheck saved = complianceRepository.save(check);
        return toResponse(saved);
    }

    public Optional<ComplianceResponse> getComplianceCheckById(Long id) {
        return complianceRepository.findById(id).map(this::toResponse);
    }

    public List<ComplianceResponse> getAllComplianceChecks() {
        return complianceRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<ComplianceResponse> getComplianceChecksByCustomerId(Long customerId) {
        return complianceRepository.findByCustomerId(customerId).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public ComplianceResponse updateComplianceCheck(Long id, ComplianceRequest request) {
        ComplianceCheck check = complianceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ComplianceCheck", id));
        check.setCheckType(CheckType.valueOf(request.getCheckType()));
        check.setStatus(CheckStatus.valueOf(request.getStatus()));
        check.setCheckedBy(request.getCheckedBy());
        check.setRemarks(request.getRemarks());
        ComplianceCheck updated = complianceRepository.save(check);
        return toResponse(updated);
    }

    public void deleteComplianceCheck(Long id) {
        complianceRepository.findById(id).ifPresentOrElse(
                complianceRepository::delete,
                () -> { throw new EntityNotFoundException("ComplianceCheck", id); }
        );
    }

    private ComplianceResponse toResponse(ComplianceCheck check) {
        return new ComplianceResponse(
                check.getId(),
                check.getCustomerId(),
                check.getCheckType().name(),
                check.getStatus().name(),
                check.getCheckedBy(),
                check.getRemarks(),
                check.getCheckDate()
        );
    }
}
