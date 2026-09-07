package com.digibank.compliance.service;

import com.digibank.compliance.dto.ComplianceRequest;
import com.digibank.compliance.dto.ComplianceResponse;
import com.digibank.compliance.dto.ComplianceSummaryResponse;
import com.digibank.compliance.model.ComplianceCheck;
import com.digibank.compliance.repository.ComplianceRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
class ComplianceServiceTest {

    @Mock
    private ComplianceRepository complianceRepository;

    @InjectMocks
    private ComplianceService complianceService;

    private ComplianceCheck checkWithId(Long id, Long customerId, String checkType,
                                         String status, String checkedBy, String remarks) {
        var check = new ComplianceCheck(customerId, checkType, status, checkedBy, remarks);
        check.setId(id);
        return check;
    }

    @Test
    void shouldCreateComplianceCheck() {
        var request = new ComplianceRequest();
        request.setCustomerId(1L);
        request.setCheckType("KYC");
        request.setStatus("PENDING");
        request.setCheckedBy("officer1");
        request.setRemarks("Initial check");

        var saved = checkWithId(1L, 1L, "KYC", "PENDING", "officer1", "Initial check");
        given(complianceRepository.save(any(ComplianceCheck.class))).willReturn(saved);

        ComplianceResponse response = complianceService.create(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getCustomerId()).isEqualTo(1L);
        assertThat(response.getCheckType()).isEqualTo("KYC");
        assertThat(response.getStatus()).isEqualTo("PENDING");
        assertThat(response.getRemarks()).isEqualTo("Initial check");
        then(complianceRepository).should().save(any(ComplianceCheck.class));
    }

    @Test
    void shouldFindAllComplianceChecks() {
        var checks = List.of(
                checkWithId(1L, 1L, "KYC", "PENDING", "officer1", "Remark 1"),
                checkWithId(2L, 2L, "AML", "APPROVED", "officer2", "Remark 2")
        );
        given(complianceRepository.findAll()).willReturn(checks);

        List<ComplianceSummaryResponse> responses = complianceService.findAll();

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getCheckType()).isEqualTo("KYC");
        assertThat(responses.get(1).getCheckType()).isEqualTo("AML");
    }

    @Test
    void shouldFindComplianceCheckById() {
        var check = checkWithId(1L, 1L, "KYC", "PENDING", "officer1", "Initial check");
        given(complianceRepository.findById(1L)).willReturn(Optional.of(check));

        ComplianceResponse response = complianceService.findById(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getCheckType()).isEqualTo("KYC");
        assertThat(response.getStatus()).isEqualTo("PENDING");
    }

    @Test
    void shouldThrowWhenComplianceCheckNotFound() {
        given(complianceRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> complianceService.findById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Compliance check not found");
    }

    @Test
    void shouldUpdateComplianceCheck() {
        var existing = checkWithId(1L, 1L, "KYC", "PENDING", "officer1", "Initial check");
        var request = new ComplianceRequest();
        request.setCustomerId(1L);
        request.setCheckType("AML");
        request.setStatus("APPROVED");
        request.setCheckedBy("officer2");
        request.setRemarks("Updated remarks");

        given(complianceRepository.findById(1L)).willReturn(Optional.of(existing));
        given(complianceRepository.save(any(ComplianceCheck.class))).willReturn(existing);

        ComplianceResponse response = complianceService.update(1L, request);

        assertThat(response.getCheckType()).isEqualTo("AML");
        assertThat(response.getStatus()).isEqualTo("APPROVED");
        assertThat(response.getRemarks()).isEqualTo("Updated remarks");
    }

    @Test
    void shouldThrowWhenUpdatingNonExistent() {
        var request = new ComplianceRequest();
        request.setCustomerId(1L);
        request.setCheckType("AML");
        request.setStatus("APPROVED");
        request.setCheckedBy("officer2");
        request.setRemarks("Updated remarks");

        given(complianceRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> complianceService.update(99L, request))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Compliance check not found");
    }

    @Test
    void shouldDeleteComplianceCheck() {
        given(complianceRepository.existsById(1L)).willReturn(true);

        complianceService.delete(1L);

        then(complianceRepository).should().deleteById(1L);
    }

    @Test
    void shouldThrowWhenDeletingNonExistent() {
        given(complianceRepository.existsById(99L)).willReturn(false);

        assertThatThrownBy(() -> complianceService.delete(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Compliance check not found");
    }

    @Test
    void shouldFindComplianceChecksByCustomerId() {
        var checks = List.of(
                checkWithId(1L, 1L, "KYC", "PENDING", "officer1", "Remark 1"),
                checkWithId(2L, 1L, "AML", "APPROVED", "officer2", "Remark 2")
        );
        given(complianceRepository.findByCustomerId(1L)).willReturn(checks);

        List<ComplianceSummaryResponse> responses = complianceService.findByCustomerId(1L);

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getCheckType()).isEqualTo("KYC");
        assertThat(responses.get(1).getCheckType()).isEqualTo("AML");
    }
}
