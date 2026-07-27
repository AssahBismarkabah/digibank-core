package com.digibank.compliance.controller;

import com.digibank.compliance.dto.ComplianceRequest;
import com.digibank.compliance.dto.ComplianceResponse;
import com.digibank.compliance.service.ComplianceService;
import com.digibank.shared.exception.GlobalExceptionHandler;
import tools.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ComplianceController.class)
@Import(GlobalExceptionHandler.class)
class ComplianceControllerTest {

    @Autowired
    private MockMvcTester mockMvcTester;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ComplianceService complianceService;

    @Test
    void shouldReturnEmptyListWhenNoComplianceChecks() throws Exception {
        given(complianceService.findAll()).willReturn(Collections.emptyList());

        var result = mockMvcTester.get()
                .uri("/api/compliance")
                .exchange();

        result.assertThat()
                .matches(status().isOk());

        ComplianceResponse[] responses = objectMapper.readValue(
                result.getResponse().getContentAsString(), ComplianceResponse[].class);
        assertThat(responses).isEmpty();
    }

    @Test
    void shouldCreateComplianceCheck() throws Exception {
        var request = new ComplianceRequest();
        request.setCustomerId(1L);
        request.setCheckType("KYC");
        request.setStatus("PENDING");
        request.setCheckedBy("officer1");
        request.setRemarks("Initial check");

        var response = new ComplianceResponse(1L, 1L, "KYC", "PENDING", "officer1",
                "Initial check", LocalDateTime.now());
        given(complianceService.create(any(ComplianceRequest.class))).willReturn(response);

        var result = mockMvcTester.post()
                .uri("/api/compliance")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .exchange();

        result.assertThat()
                .matches(status().isCreated());

        ComplianceResponse body = objectMapper.readValue(
                result.getResponse().getContentAsString(), ComplianceResponse.class);
        assertThat(body.getId()).isEqualTo(1L);
        assertThat(body.getCheckType()).isEqualTo("KYC");
    }

    @Test
    void shouldReturnComplianceCheckById() throws Exception {
        var response = new ComplianceResponse(1L, 1L, "KYC", "PENDING", "officer1",
                "Initial check", LocalDateTime.now());
        given(complianceService.findById(1L)).willReturn(response);

        var result = mockMvcTester.get()
                .uri("/api/compliance/1")
                .exchange();

        result.assertThat()
                .matches(status().isOk());

        ComplianceResponse body = objectMapper.readValue(
                result.getResponse().getContentAsString(), ComplianceResponse.class);
        assertThat(body.getCheckType()).isEqualTo("KYC");
        assertThat(body.getStatus()).isEqualTo("PENDING");
    }

    @Test
    void shouldReturn404WhenComplianceCheckNotFound() {
        given(complianceService.findById(99L))
                .willThrow(new EntityNotFoundException("Compliance check not found with id: 99"));

        mockMvcTester.get()
                .uri("/api/compliance/99")
                .exchange()
                .assertThat()
                .matches(status().isNotFound());
    }

    @Test
    void shouldUpdateComplianceCheck() throws Exception {
        var request = new ComplianceRequest();
        request.setCustomerId(1L);
        request.setCheckType("AML");
        request.setStatus("APPROVED");
        request.setCheckedBy("officer2");
        request.setRemarks("Updated remarks");

        var response = new ComplianceResponse(1L, 1L, "AML", "APPROVED", "officer2",
                "Updated remarks", LocalDateTime.now());
        given(complianceService.update(eq(1L), any(ComplianceRequest.class))).willReturn(response);

        var result = mockMvcTester.put()
                .uri("/api/compliance/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .exchange();

        result.assertThat()
                .matches(status().isOk());

        ComplianceResponse body = objectMapper.readValue(
                result.getResponse().getContentAsString(), ComplianceResponse.class);
        assertThat(body.getCheckType()).isEqualTo("AML");
        assertThat(body.getStatus()).isEqualTo("APPROVED");
    }

    @Test
    void shouldDeleteComplianceCheck() {
        mockMvcTester.delete()
                .uri("/api/compliance/1")
                .exchange()
                .assertThat()
                .matches(status().isNoContent());
    }

    @Test
    void shouldReturn404WhenDeletingNonExistent() {
        willThrow(new EntityNotFoundException("Compliance check not found with id: 99"))
                .given(complianceService).delete(99L);

        mockMvcTester.delete()
                .uri("/api/compliance/99")
                .exchange()
                .assertThat()
                .matches(status().isNotFound());
    }

    @Test
    void shouldReturnComplianceChecksByCustomerId() throws Exception {
        var checks = List.of(
                new ComplianceResponse(1L, 1L, "KYC", "PENDING", "officer1", "Remark 1", LocalDateTime.now()),
                new ComplianceResponse(2L, 1L, "AML", "APPROVED", "officer2", "Remark 2", LocalDateTime.now())
        );
        given(complianceService.findByCustomerId(1L)).willReturn(checks);

        var result = mockMvcTester.get()
                .uri("/api/compliance/by-customer/1")
                .exchange();

        result.assertThat()
                .matches(status().isOk());

        ComplianceResponse[] responses = objectMapper.readValue(
                result.getResponse().getContentAsString(), ComplianceResponse[].class);
        assertThat(responses).hasSize(2);
        assertThat(responses[0].getCheckType()).isEqualTo("KYC");
        assertThat(responses[1].getCheckType()).isEqualTo("AML");
    }
}
