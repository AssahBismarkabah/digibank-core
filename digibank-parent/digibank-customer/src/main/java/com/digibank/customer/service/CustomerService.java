package com.digibank.customer.service;

import com.digibank.customer.dto.CustomerRequest;
import com.digibank.customer.dto.CustomerResponse;
import com.digibank.customer.model.Customer;
import com.digibank.customer.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public CustomerResponse create(CustomerRequest request) {
        var firstName = normalizeName(request.getFirstName());
        var lastName = normalizeName(request.getLastName());
        var email = normalizeEmail(request.getEmail());
        if (customerRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email already exists: " + email);
        }
        var customer = new Customer(firstName, lastName, email);
        customer = customerRepository.save(customer);
        return toResponse(customer);
    }

    @Transactional(readOnly = true)
    public List<CustomerResponse> findAll() {
        return customerRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public CustomerResponse findById(Long id) {
        return customerRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + id));
    }

    public CustomerResponse update(Long id, CustomerRequest request) {
        var customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer not found with id: " + id));
        customer.setFirstName(normalizeName(request.getFirstName()));
        customer.setLastName(normalizeName(request.getLastName()));
        customer.setEmail(normalizeEmail(request.getEmail()));
        customer = customerRepository.save(customer);
        return toResponse(customer);
    }

    public void delete(Long id) {
        if (!customerRepository.existsById(id)) {
            throw new EntityNotFoundException("Customer not found with id: " + id);
        }
        customerRepository.deleteById(id);
    }

    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(customer.getId(), customer.getFirstName(),
                customer.getLastName(), customer.getEmail());
    }

    private String normalizeName(String value) {
        return value.trim().replaceAll("\\s+", " ");
    }

    private String normalizeEmail(String value) {
        return value.trim().toLowerCase(java.util.Locale.ROOT);
    }
}
