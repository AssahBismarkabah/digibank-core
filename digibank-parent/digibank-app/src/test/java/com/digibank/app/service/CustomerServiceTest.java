package com.digibank.app.service;

import com.digibank.customer.dto.CustomerRequest;
import com.digibank.customer.dto.CustomerResponse;
import com.digibank.customer.model.Customer;
import com.digibank.customer.repository.CustomerRepository;
import com.digibank.customer.service.CustomerService;
import jakarta.enterprise.inject.Produces;
import jakarta.inject.Inject;
import org.jboss.weld.junit.MockBean;
import org.jboss.weld.junit5.EnableWeld;
import org.jboss.weld.junit5.WeldInitiator;
import org.jboss.weld.junit5.WeldSetup;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@EnableWeld
class CustomerServiceTest {

    private static final CustomerRepository customerRepo = mock(CustomerRepository.class);

    @WeldSetup
    WeldInitiator weld = WeldInitiator.from(CustomerService.class)
            .addBeans(MockBean.builder()
                    .types(CustomerRepository.class)
                    .creating(customerRepo)
                    .build())
            .build();

    @Inject
    private CustomerService customerService;

    @Test
    void shouldCreateCustomer() {
        CustomerRequest request = new CustomerRequest("Alice", "Smith", "alice@example.com");
        Customer saved = new Customer("Alice", "Smith", "alice@example.com");
        saved.setId(1L);

        when(customerRepo.save(any(Customer.class))).thenReturn(saved);

        CustomerResponse response = customerService.createCustomer(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Alice", response.getFirstName());
        assertEquals("Smith", response.getLastName());
        assertEquals("alice@example.com", response.getEmail());
    }

    @Test
    void shouldReturnCustomerById() {
        Customer customer = new Customer("Bob", "Jones", "bob@example.com");
        customer.setId(2L);

        when(customerRepo.findById(2L)).thenReturn(Optional.of(customer));

        Optional<CustomerResponse> found = customerService.getCustomerById(2L);

        assertTrue(found.isPresent());
        assertEquals("Bob", found.get().getFirstName());
        assertEquals("bob@example.com", found.get().getEmail());
    }

    @Test
    void shouldReturnEmptyWhenCustomerNotFound() {
        when(customerRepo.findById(99L)).thenReturn(Optional.empty());

        Optional<CustomerResponse> found = customerService.getCustomerById(99L);

        assertTrue(found.isEmpty());
    }

    @Test
    void shouldReturnAllCustomers() {
        Customer c1 = new Customer("User", "One", "u1@example.com");
        c1.setId(1L);
        Customer c2 = new Customer("User", "Two", "u2@example.com");
        c2.setId(2L);

        when(customerRepo.findAll()).thenReturn(List.of(c1, c2));

        List<CustomerResponse> customers = customerService.getAllCustomers();

        assertEquals(2, customers.size());
    }
}
