package com.digibank.customer.service;

import com.digibank.customer.dto.CustomerRequest;
import com.digibank.customer.dto.CustomerResponse;
import com.digibank.customer.model.Customer;
import com.digibank.customer.repository.CustomerRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {

    @Mock
    private CustomerRepository customerRepository;

    @InjectMocks
    private CustomerService customerService;

    private Customer customerWithId(Long id, String firstName, String lastName, String email) {
        var c = new Customer(firstName, lastName, email);
        c.setId(id);
        return c;
    }

    @Test
    void shouldCreateCustomer() {
        var request = new CustomerRequest("John", "Doe", "john@example.com");
        var saved = customerWithId(1L, "John", "Doe", "john@example.com");
        given(customerRepository.existsByEmail("john@example.com")).willReturn(false);
        given(customerRepository.save(any(Customer.class))).willReturn(saved);

        CustomerResponse response = customerService.create(request);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getFirstName()).isEqualTo("John");
        assertThat(response.getEmail()).isEqualTo("john@example.com");
        then(customerRepository).should().save(any(Customer.class));
    }

    @Test
    void shouldRejectDuplicateEmail() {
        var request = new CustomerRequest("John", "Doe", "existing@example.com");
        given(customerRepository.existsByEmail("existing@example.com")).willReturn(true);

        assertThatThrownBy(() -> customerService.create(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Email already exists");

        then(customerRepository).should(never()).save(any(Customer.class));
    }

    @Test
    void shouldFindAllCustomers() {
        var customers = List.of(
                customerWithId(1L, "Alice", "Smith", "alice@example.com"),
                customerWithId(2L, "Bob", "Jones", "bob@example.com")
        );
        given(customerRepository.findAll()).willReturn(customers);

        List<CustomerResponse> responses = customerService.findAll();

        assertThat(responses).hasSize(2);
        assertThat(responses.get(0).getFirstName()).isEqualTo("Alice");
    }

    @Test
    void shouldFindCustomerById() {
        var customer = customerWithId(1L, "Alice", "Smith", "alice@example.com");
        given(customerRepository.findById(1L)).willReturn(Optional.of(customer));

        CustomerResponse response = customerService.findById(1L);

        assertThat(response.getFirstName()).isEqualTo("Alice");
        assertThat(response.getEmail()).isEqualTo("alice@example.com");
    }

    @Test
    void shouldThrowWhenCustomerNotFound() {
        given(customerRepository.findById(99L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> customerService.findById(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Customer not found");
    }

    @Test
    void shouldUpdateCustomer() {
        var existing = customerWithId(1L, "Old", "Name", "old@example.com");
        var request = new CustomerRequest("New", "Name", "new@example.com");
        given(customerRepository.findById(1L)).willReturn(Optional.of(existing));
        given(customerRepository.save(any(Customer.class))).willReturn(existing);

        CustomerResponse response = customerService.update(1L, request);

        assertThat(response.getFirstName()).isEqualTo("New");
        assertThat(response.getEmail()).isEqualTo("new@example.com");
    }

    @Test
    void shouldDeleteCustomer() {
        given(customerRepository.existsById(1L)).willReturn(true);

        customerService.delete(1L);

        then(customerRepository).should().deleteById(1L);
    }

    @Test
    void shouldThrowWhenDeletingNonExistent() {
        given(customerRepository.existsById(99L)).willReturn(false);

        assertThatThrownBy(() -> customerService.delete(99L))
                .isInstanceOf(EntityNotFoundException.class)
                .hasMessageContaining("Customer not found");
    }
}
