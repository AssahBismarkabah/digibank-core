package com.digibank.customer.service;

import com.digibank.customer.model.Customer;
import com.digibank.customer.repository.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

class CustomerServiceTest {
    @Test
    void findsCustomerById() {
        CustomerRepository repository = Mockito.mock(CustomerRepository.class);
        Customer customer = new Customer("Ada", "Lovelace", "ada@example.com");
        when(repository.findById(1L)).thenReturn(java.util.Optional.of(customer));
        assertEquals("ada@example.com", new CustomerService(repository).findById(1L).email());
    }
}
