package com.digibank.customer.controller;

import com.digibank.customer.dto.CustomerRequest;
import com.digibank.customer.dto.CustomerResponse;
import com.digibank.customer.service.CustomerService;
import com.digibank.shared.exception.GlobalExceptionHandler;
import tools.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CustomerController.class)
@Import(GlobalExceptionHandler.class)
class CustomerControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CustomerService customerService;

    @Test
    void shouldListAllCustomers() throws Exception {
        var customers = List.of(
                new CustomerResponse(1L, "Alice", "Smith", "alice@example.com"),
                new CustomerResponse(2L, "Bob", "Jones", "bob@example.com")
        );
        given(customerService.findAll()).willReturn(customers);

        mockMvc.perform(get("/api/customers"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].firstName").value("Alice"));
    }

    @Test
    void shouldReturnCustomerById() throws Exception {
        var customer = new CustomerResponse(1L, "Alice", "Smith", "alice@example.com");
        given(customerService.findById(1L)).willReturn(customer);

        mockMvc.perform(get("/api/customers/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Alice"))
                .andExpect(jsonPath("$.email").value("alice@example.com"));
    }

    @Test
    void shouldReturn404WhenCustomerNotFound() throws Exception {
        given(customerService.findById(99L))
                .willThrow(new EntityNotFoundException("Customer not found with id: 99"));

        mockMvc.perform(get("/api/customers/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Resource not found"))
                .andExpect(jsonPath("$.details[0]").value("The requested resource does not exist"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(content().string(not(containsString("id: 99"))))
                .andExpect(content().string(not(containsString("Customer not found with id"))));
    }

    @Test
    void shouldReturnGenericBusinessErrorWithoutSubmittedValue() throws Exception {
        var request = new CustomerRequest("John", "Doe", "john@example.com");
        given(customerService.create(any(CustomerRequest.class)))
                .willThrow(new IllegalArgumentException("Email already exists: john@example.com"));

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Request could not be processed"))
                .andExpect(jsonPath("$.details[0]").value("Business rule validation failed"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(content().string(not(containsString("john@example.com"))))
                .andExpect(content().string(not(containsString("Email already exists"))));
    }

    @Test
    void shouldReturnGenericValidationErrorForInvalidPathVariable() throws Exception {
        mockMvc.perform(get("/api/customers/not-a-number"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Input validation failed"))
                .andExpect(jsonPath("$.details[0]").value("One or more request parameters are invalid"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(content().string(not(containsString("not-a-number"))))
                .andExpect(content().string(not(containsString("MethodArgumentTypeMismatchException"))));
    }

    @Test
    void shouldCreateCustomer() throws Exception {
        var request = new CustomerRequest("John", "Doe", "john@example.com");
        var response = new CustomerResponse(1L, "John", "Doe", "john@example.com");
        given(customerService.create(any(CustomerRequest.class))).willReturn(response);

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void shouldReturn400ForInvalidInput() throws Exception {
        var request = new CustomerRequest("", "", "invalid-email");

        mockMvc.perform(post("/api/customers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Input validation failed"))
                .andExpect(jsonPath("$.details", hasItem("Invalid field: firstName")))
                .andExpect(jsonPath("$.details", hasItem("Invalid field: lastName")))
                .andExpect(jsonPath("$.details", hasItem("Invalid field: email")))
                .andExpect(jsonPath("$.violations").doesNotExist())
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(content().string(not(containsString("invalid-email"))))
                .andExpect(content().string(not(containsString("Email must be a valid format"))));
    }

    @Test
    void shouldUpdateCustomer() throws Exception {
        var request = new CustomerRequest("New", "Name", "new@example.com");
        var response = new CustomerResponse(1L, "New", "Name", "new@example.com");
        given(customerService.update(eq(1L), any(CustomerRequest.class))).willReturn(response);

        mockMvc.perform(put("/api/customers/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("New"));
    }

    @Test
    void shouldDeleteCustomer() throws Exception {
        mockMvc.perform(delete("/api/customers/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturn404WhenDeletingNonExistent() throws Exception {
        willThrow(new EntityNotFoundException("Customer not found with id: 99"))
                .given(customerService).delete(99L);

        mockMvc.perform(delete("/api/customers/99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.message").value("Resource not found"))
                .andExpect(jsonPath("$.details[0]").value("The requested resource does not exist"))
                .andExpect(jsonPath("$.timestamp").exists())
                .andExpect(content().string(not(containsString("id: 99"))));
    }
}
