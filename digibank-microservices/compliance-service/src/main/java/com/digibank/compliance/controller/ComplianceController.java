package com.digibank.compliance.controller;
import com.digibank.compliance.dto.*;
import com.digibank.compliance.service.ComplianceService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController @RequestMapping("/api/compliance") public class ComplianceController {
    private final ComplianceService service;
    public ComplianceController(ComplianceService service) { this.service = service; }
    @GetMapping public List<ComplianceSummaryResponse> findAll() { return service.findAll(); }
    @GetMapping("/{id}") public ComplianceResponse findById(@PathVariable Long id) { return service.findById(id); }
    @GetMapping("/by-customer/{customerId}") public List<ComplianceSummaryResponse> findByCustomerId(@PathVariable Long customerId) { return service.findByCustomerId(customerId); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED) public ComplianceResponse create(@Valid @RequestBody ComplianceRequest request) { return service.create(request); }
    @PutMapping("/{id}") public ComplianceResponse update(@PathVariable Long id, @Valid @RequestBody ComplianceRequest request) { return service.update(id, request); }
    @DeleteMapping("/{id}") @ResponseStatus(HttpStatus.NO_CONTENT) public void delete(@PathVariable Long id) { service.delete(id); }
}
