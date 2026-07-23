package com.digibank.customer.steps;

import com.digibank.customer.dto.CustomerRequest;
import com.digibank.customer.dto.CustomerResponse;
import com.digibank.customer.model.Customer;
import com.digibank.customer.repository.CustomerRepository;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import org.springframework.test.web.servlet.assertj.MvcTestResult;
import tools.jackson.databind.ObjectMapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class CustomerStepDefinitions {

    @Autowired
    private MockMvcTester mockMvcTester;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private CustomerRepository customerRepository;

    private MvcTestResult result;
    private CustomerRequest currentRequest;
    private Long lastCreatedCustomerId;

    @Given("I am on the customer creation page")
    public void i_am_on_the_customer_creation_page() {
        // Navigation state -- no action needed for REST API testing
    }

    @When("I enter {string} as first name and {string} as last name and {string} as email")
    public void i_enter_customer_details(String firstName, String lastName, String email) {
        currentRequest = new CustomerRequest(firstName, lastName, email);
    }

    @When("I click the {string} button")
    public void i_click_the_button(String button) throws Exception {
        if ("Create Customer".equals(button)) {
            result = mockMvcTester.post()
                .uri("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(currentRequest))
                .exchange();
        }
    }

    @Then("a new customer with email {string} should be created")
    public void a_new_customer_should_be_created(String email) throws Exception {
        result.assertThat().matches(status().isCreated());
        CustomerResponse body = objectMapper.readValue(
            result.getResponse().getContentAsString(), CustomerResponse.class);
        assertThat(body.getEmail()).isEqualTo(email);
        assertThat(body.getId()).isNotNull();
    }

    @Then("the system should reject the request with a validation error")
    public void the_system_should_reject_with_validation_error() throws Exception {
        result = mockMvcTester.post()
            .uri("/api/customers")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(currentRequest))
            .exchange();
        result.assertThat().matches(status().isBadRequest());
    }

    @Given("a customer with email {string} exists in the system")
    public void a_customer_exists(String email) {
        Customer customer = new Customer("Alice", "Smith", email);
        lastCreatedCustomerId = customerRepository.save(customer).getId();
    }

    @When("I request the customer details")
    public void i_request_customer_details() {
        result = mockMvcTester.get()
            .uri("/api/customers/" + lastCreatedCustomerId)
            .exchange();
    }

    @Then("I should receive the customer information with first name {string}")
    public void i_should_receive_customer_info(String firstName) throws Exception {
        result.assertThat().matches(status().isOk());
        CustomerResponse body = objectMapper.readValue(
            result.getResponse().getContentAsString(), CustomerResponse.class);
        assertThat(body.getFirstName()).isEqualTo(firstName);
    }

    @Given("multiple customers exist in the system")
    public void multiple_customers_exist() {
        customerRepository.save(new Customer("Alice", "Smith", "alice@example.com"));
        customerRepository.save(new Customer("Bob", "Jones", "bob@example.com"));
    }

    @When("I request the list of all customers")
    public void i_request_all_customers() {
        result = mockMvcTester.get()
            .uri("/api/customers")
            .exchange();
    }

    @Then("I should receive a list containing at least {int} customers")
    public void i_should_receive_customer_list(int count) throws Exception {
        result.assertThat().matches(status().isOk());
        CustomerResponse[] body = objectMapper.readValue(
            result.getResponse().getContentAsString(), CustomerResponse[].class);
        assertThat(body).hasSizeGreaterThanOrEqualTo(count);
    }
}
