package com.digibank.compliance.service;

import com.digibank.compliance.repository.ComplianceRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ComplianceServiceTest {
    @Test
    void rejectsMissingComplianceCheck() {
        ComplianceRepository repository = Mockito.mock(ComplianceRepository.class);
        Mockito.when(repository.findById(1L)).thenReturn(java.util.Optional.empty());
        assertThrows(jakarta.persistence.EntityNotFoundException.class, () -> new ComplianceService(repository).findById(1L));
    }
}
