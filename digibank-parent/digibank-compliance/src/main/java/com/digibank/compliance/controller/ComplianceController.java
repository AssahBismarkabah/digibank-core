package com.digibank.compliance.controller;

import com.digibank.compliance.dto.ComplianceRequest;
import com.digibank.compliance.dto.ComplianceResponse;
import com.digibank.compliance.dto.ComplianceSummaryResponse;
import com.digibank.compliance.service.ComplianceService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.List;

@RestController
@RequestMapping("/api/compliance")
public class ComplianceController {

    private final ComplianceService complianceService;

    public ComplianceController(ComplianceService complianceService) {
        this.complianceService = complianceService;
    }

    @GetMapping
    public ResponseEntity<List<ComplianceSummaryResponse>> findAll() {
        return ResponseEntity.ok(complianceService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ComplianceResponse> findById(@PathVariable Long id) {
        return ResponseEntity.ok(complianceService.findById(id));
    }

    @GetMapping("/by-customer/{customerId}")
    public ResponseEntity<List<ComplianceSummaryResponse>> findByCustomerId(@PathVariable Long customerId) {
        return ResponseEntity.ok(complianceService.findByCustomerId(customerId));
    }

    @PostMapping
    public ResponseEntity<ComplianceResponse> create(@Valid @RequestBody ComplianceRequest request) {
        var response = complianceService.create(request);
        var location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.getId())
                .toUri();
        return ResponseEntity
                .created(location)
                .body(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ComplianceResponse> update(@PathVariable Long id,
                                                      @Valid @RequestBody ComplianceRequest request) {
        return ResponseEntity.ok(complianceService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        complianceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
