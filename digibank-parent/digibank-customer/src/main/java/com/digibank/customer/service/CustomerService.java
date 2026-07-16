package com.digibank.customer.service;

import com.digibank.customer.dto.CustomerRequest;
import com.digibank.customer.dto.CustomerResponse;
import com.digibank.customer.model.Customer;
import com.digibank.customer.repository.CustomerRepository;
import com.digibank.shared.exception.EntityNotFoundException;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Stateless
public class CustomerService {

    @Inject
    private CustomerRepository customerRepository;

    public CustomerResponse createCustomer(CustomerRequest request) {
        Customer customer = new Customer(request.getFirstName(), request.getLastName(), request.getEmail());
        Customer saved = customerRepository.save(customer);
        return toResponse(saved);
    }

    public Optional<CustomerResponse> getCustomerById(Long id) {
        return customerRepository.findById(id).map(this::toResponse);
    }

    public List<CustomerResponse> getAllCustomers() {
        return customerRepository.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public CustomerResponse updateCustomer(Long id, CustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Customer", id));
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        Customer updated = customerRepository.save(customer);
        return toResponse(updated);
    }

    public void deleteCustomer(Long id) {
        customerRepository.findById(id).ifPresentOrElse(
                customerRepository::delete,
                () -> { throw new EntityNotFoundException("Customer", id); }
        );
    }

    private CustomerResponse toResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getFirstName(),
                customer.getLastName(),
                customer.getEmail()
        );
    }
}
