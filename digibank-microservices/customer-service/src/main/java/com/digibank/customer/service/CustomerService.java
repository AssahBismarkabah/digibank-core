package com.digibank.customer.service;

import com.digibank.customer.dto.CustomerRequest;
import com.digibank.customer.dto.CustomerResponse;
import com.digibank.customer.model.Customer;
import com.digibank.customer.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
@Transactional
public class CustomerService {
    private final CustomerRepository repository;

    public CustomerService(CustomerRepository repository) { this.repository = repository; }

    public CustomerResponse create(CustomerRequest request) {
        String email = normalizeEmail(request.email());
        if (repository.existsByEmail(email)) throw new IllegalArgumentException("Email already exists");
        return toResponse(repository.save(new Customer(normalize(request.firstName()), normalize(request.lastName()), email)));
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> findAll() { return repository.findAll().stream().map(this::toResponse).toList(); }

    @Transactional(readOnly = true)
    public CustomerResponse findById(Long id) { return toResponse(repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Customer not found"))); }

    public CustomerResponse update(Long id, CustomerRequest request) {
        Customer customer = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Customer not found"));
        customer.update(normalize(request.firstName()), normalize(request.lastName()), normalizeEmail(request.email()));
        return toResponse(customer);
    }

    public void delete(Long id) {
        if (!repository.existsById(id)) throw new EntityNotFoundException("Customer not found");
        repository.deleteById(id);
    }

    private CustomerResponse toResponse(Customer customer) { return new CustomerResponse(customer.getId(), customer.getFirstName(), customer.getLastName(), customer.getEmail()); }
    private String normalize(String value) { return value.trim().replaceAll("\\s+", " "); }
    private String normalizeEmail(String value) { return value.trim().toLowerCase(Locale.ROOT); }
}
