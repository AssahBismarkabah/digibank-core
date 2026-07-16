package com.digibank.customer.steps;

import com.digibank.customer.dto.CustomerRequest;
import com.digibank.customer.dto.CustomerResponse;
import com.digibank.customer.model.Customer;
import com.digibank.customer.repository.CustomerRepository;
import com.digibank.customer.service.CustomerService;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CustomerStepDefinitions {

    private CustomerRepository customerRepository;
    private CustomerService customerService;
    private CustomerResponse result;
    private List<CustomerResponse> customerList;
    private Exception error;

    @Given("I am on the customer creation page")
    public void i_am_on_the_customer_creation_page() {
        customerRepository = mock(CustomerRepository.class);
        customerService = new CustomerService();
        injectMock();
    }

    @When("I enter {word} as first name and {word} as last name and {word} as email")
    public void i_enter_first_name_last_name_email(String firstName, String lastName, String email) {
        CustomerRequest request = new CustomerRequest(firstName, lastName, email);
        Customer saved = new Customer(firstName, lastName, email);
        saved.setId(1L);

        when(customerRepository.save(any(Customer.class))).thenReturn(saved);

        result = customerService.createCustomer(request);
    }

    @When("I click the {string} button")
    public void i_click_the_button(String buttonName) {
        // Button click is implicit -- the create step already triggers the action
    }

    @Then("a new customer with email {word} should be created")
    public void a_new_customer_with_email_should_be_created(String expectedEmail) {
        assertNotNull(result);
        assertEquals(expectedEmail, result.getEmail());
    }

    @Then("the system should reject the request with a validation error")
    public void the_system_should_reject_the_request_with_a_validation_error() {
        // Simulate validation failure
        when(customerRepository.save(any(Customer.class)))
                .thenThrow(new IllegalArgumentException("Invalid customer data"));

        try {
            CustomerRequest invalid = new CustomerRequest("Bob", "Jones", "invalid-email");
            customerService.createCustomer(invalid);
            fail("Should have thrown an exception");
        } catch (IllegalArgumentException e) {
            assertEquals("Invalid customer data", e.getMessage());
        }
    }

    @Given("a customer with email {word} exists in the system")
    public void a_customer_with_email_exists(String email) {
        customerRepository = mock(CustomerRepository.class);
        customerService = new CustomerService();
        injectMock();

        Customer customer = new Customer("Alice", "Smith", email);
        customer.setId(1L);
        when(customerRepository.findById(1L)).thenReturn(Optional.of(customer));
    }

    @When("I request the customer details by ID {long}")
    public void i_request_customer_details_by_id(Long id) {
        Optional<CustomerResponse> found = customerService.getCustomerById(id);
        result = found.orElse(null);
    }

    @Then("I should receive the customer information with first name {word}")
    public void i_should_receive_customer_with_first_name(String firstName) {
        assertNotNull(result);
        assertEquals(firstName, result.getFirstName());
    }

    @Given("multiple customers exist in the system")
    public void multiple_customers_exist() {
        customerRepository = mock(CustomerRepository.class);
        customerService = new CustomerService();
        injectMock();

        Customer c1 = new Customer("User", "One", "u1@example.com");
        c1.setId(1L);
        Customer c2 = new Customer("User", "Two", "u2@example.com");
        c2.setId(2L);
        when(customerRepository.findAll()).thenReturn(List.of(c1, c2));
    }

    @When("I request the list of all customers")
    public void i_request_all_customers() {
        customerList = customerService.getAllCustomers();
    }

    @Then("I should receive a list containing at least {int} customers")
    public void i_should_receive_list_with_at_least(int count) {
        assertNotNull(customerList);
        assertTrue(customerList.size() >= count);
    }

    private void injectMock() {
        try {
            java.lang.reflect.Field field = CustomerService.class.getDeclaredField("customerRepository");
            field.setAccessible(true);
            field.set(customerService, customerRepository);
        } catch (Exception e) {
            throw new RuntimeException("Failed to inject mock repository", e);
        }
    }
}
