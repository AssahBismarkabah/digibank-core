package com.digibank.account.controller;

import com.digibank.account.dto.AccountRequest;
import com.digibank.account.dto.AccountResponse;
import com.digibank.account.dto.AccountSummaryResponse;
import com.digibank.account.service.AccountService;
import com.digibank.shared.exception.GlobalExceptionHandler;
import tools.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AccountController.class)
@Import(GlobalExceptionHandler.class)
class AccountControllerTest {

    @Autowired
    private MockMvcTester mockMvcTester;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AccountService accountService;

    @Test
    void shouldReturnEmptyListWhenNoAccounts() throws Exception {
        given(accountService.findAll()).willReturn(Collections.emptyList());

        var result = mockMvcTester.get()
                .uri("/api/accounts")
                .exchange();

        result.assertThat()
                .matches(status().isOk());

        AccountSummaryResponse[] responses = objectMapper.readValue(
                result.getResponse().getContentAsString(), AccountSummaryResponse[].class);
        assertThat(responses).isEmpty();
    }

    @Test
    void shouldCreateAccount() throws Exception {
        var request = new AccountRequest();
        request.setAccountNumber("ACC001");
        request.setBalance(new BigDecimal("1000.00"));
        request.setCustomerId(1L);
        request.setAccountType("SAVINGS");
        request.setCurrency("USD");

        var response = new AccountResponse(1L, "****C001", new BigDecimal("1000.00"), 1L, "SAVINGS", "USD");
        given(accountService.create(any(AccountRequest.class))).willReturn(response);

        var result = mockMvcTester.post()
                .uri("/api/accounts")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .exchange();

        result.assertThat()
                .matches(status().isCreated());

        AccountResponse body = objectMapper.readValue(
                result.getResponse().getContentAsString(), AccountResponse.class);
        assertThat(body.getId()).isEqualTo(1L);
        assertThat(body.getMaskedAccountNumber()).isEqualTo("****C001");
        assertThat(result.getResponse().getContentAsString()).doesNotContain("\"accountNumber\"");
    }

    @Test
    void shouldReturnAccountById() throws Exception {
        var response = new AccountResponse(1L, "****C001", new BigDecimal("1000.00"), 1L, "SAVINGS", "USD");
        given(accountService.findById(1L)).willReturn(response);

        var result = mockMvcTester.get()
                .uri("/api/accounts/1")
                .exchange();

        result.assertThat()
                .matches(status().isOk());

        AccountResponse body = objectMapper.readValue(
                result.getResponse().getContentAsString(), AccountResponse.class);
        assertThat(body.getMaskedAccountNumber()).isEqualTo("****C001");
        assertThat(body.getBalance()).isEqualByComparingTo(new BigDecimal("1000.00"));
    }

    @Test
    void shouldReturn404WhenAccountNotFound() {
        given(accountService.findById(99L))
                .willThrow(new EntityNotFoundException("Account not found with id: 99"));

        mockMvcTester.get()
                .uri("/api/accounts/99")
                .exchange()
                .assertThat()
                .matches(status().isNotFound());
    }

    @Test
    void shouldUpdateAccount() throws Exception {
        var request = new AccountRequest();
        request.setAccountNumber("ACC001");
        request.setBalance(new BigDecimal("2000.00"));
        request.setCustomerId(1L);
        request.setAccountType("CHECKING");
        request.setCurrency("USD");

        var response = new AccountResponse(1L, "****C001", new BigDecimal("2000.00"), 1L, "CHECKING", "USD");
        given(accountService.update(eq(1L), any(AccountRequest.class))).willReturn(response);

        var result = mockMvcTester.put()
                .uri("/api/accounts/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .exchange();

        result.assertThat()
                .matches(status().isOk());

        AccountResponse body = objectMapper.readValue(
                result.getResponse().getContentAsString(), AccountResponse.class);
        assertThat(body.getAccountType()).isEqualTo("CHECKING");
        assertThat(body.getBalance()).isEqualByComparingTo(new BigDecimal("2000.00"));
    }

    @Test
    void shouldDeleteAccount() {
        mockMvcTester.delete()
                .uri("/api/accounts/1")
                .exchange()
                .assertThat()
                .matches(status().isNoContent());
    }

    @Test
    void shouldReturn404WhenDeletingNonExistent() {
        willThrow(new EntityNotFoundException("Account not found with id: 99"))
                .given(accountService).delete(99L);

        mockMvcTester.delete()
                .uri("/api/accounts/99")
                .exchange()
                .assertThat()
                .matches(status().isNotFound());
    }

    @Test
    void shouldReturnAccountsByCustomerId() throws Exception {
        var accounts = List.of(
                new AccountSummaryResponse(1L, "****C001", "SAVINGS", "USD"),
                new AccountSummaryResponse(2L, "****C002", "CHECKING", "USD")
        );
        given(accountService.findByCustomerId(1L)).willReturn(accounts);

        var result = mockMvcTester.get()
                .uri("/api/accounts/by-customer/1")
                .exchange();

        result.assertThat()
                .matches(status().isOk());

        AccountSummaryResponse[] responses = objectMapper.readValue(
                result.getResponse().getContentAsString(), AccountSummaryResponse[].class);
        assertThat(responses).hasSize(2);
        assertThat(responses[0].getMaskedAccountNumber()).isEqualTo("****C001");
        assertThat(responses[1].getMaskedAccountNumber()).isEqualTo("****C002");
        assertThat(result.getResponse().getContentAsString()).doesNotContain("\"balance\"");
        assertThat(result.getResponse().getContentAsString()).doesNotContain("\"customerId\"");
    }
}
