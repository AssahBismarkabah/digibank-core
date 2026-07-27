package com.digibank.transaction.controller;

import com.digibank.shared.exception.GlobalExceptionHandler;
import com.digibank.transaction.TestDigiBankTransactionApplication;
import com.digibank.transaction.dto.TransactionRequest;
import com.digibank.transaction.dto.TransactionResponse;
import com.digibank.transaction.service.TransactionService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.assertj.MockMvcTester;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = TransactionController.class)
@ContextConfiguration(classes = TestDigiBankTransactionApplication.class)
@Import(GlobalExceptionHandler.class)
class TransactionControllerTest {

    @Autowired
    private MockMvcTester mockMvcTester;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransactionService transactionService;

    @Test
    void shouldReturnEmptyListWhenNoTransactionsExist() throws Exception {
        given(transactionService.findAll()).willReturn(List.of());

        var result = mockMvcTester.get()
                .uri("/api/transactions")
                .exchange();

        result.assertThat().matches(status().isOk());
        assertThat(result.getResponse().getContentAsString()).isEqualTo("[]");
    }

    @Test
    void shouldCreateTransaction() throws Exception {
        var request = new TransactionRequest();
        request.setAccountId(1L);
        request.setAmount(new BigDecimal("50.00"));
        request.setTransactionType("DEPOSIT");
        request.setDescription("Salary");

        var response = new TransactionResponse(1L, 1L, new BigDecimal("50.00"),
                "DEPOSIT", "Salary", "ref-1", LocalDateTime.now());
        given(transactionService.create(any(TransactionRequest.class))).willReturn(response);

        var result = mockMvcTester.post()
                .uri("/api/transactions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .exchange();

        result.assertThat().matches(status().isCreated());
        assertThat(result.getResponse().getHeader("Location")).contains("/api/transactions/1");
        assertThat(result.getResponse().getContentAsString()).contains("\"id\":1");
    }

    @Test
    void shouldReturnTransactionById() throws Exception {
        var response = new TransactionResponse(1L, 1L, new BigDecimal("50.00"),
                "DEPOSIT", "Salary", "ref-1", LocalDateTime.now());
        given(transactionService.findById(1L)).willReturn(response);

        var result = mockMvcTester.get()
                .uri("/api/transactions/1")
                .exchange();

        result.assertThat().matches(status().isOk());
        assertThat(result.getResponse().getContentAsString()).contains("\"description\":\"Salary\"");
    }

    @Test
    void shouldReturn404WhenTransactionNotFound() throws Exception {
        given(transactionService.findById(99L))
                .willThrow(new EntityNotFoundException("Transaction not found with id: 99"));

        var result = mockMvcTester.get()
                .uri("/api/transactions/99")
                .exchange();

        result.assertThat().matches(status().isNotFound());
    }

    @Test
    void shouldUpdateTransaction() throws Exception {
        var request = new TransactionRequest();
        request.setAccountId(1L);
        request.setAmount(new BigDecimal("75.00"));
        request.setTransactionType("DEPOSIT");
        request.setDescription("Updated salary");

        var response = new TransactionResponse(1L, 1L, new BigDecimal("75.00"),
                "DEPOSIT", "Updated salary", "ref-1", LocalDateTime.now());
        given(transactionService.update(eq(1L), any(TransactionRequest.class))).willReturn(response);

        var result = mockMvcTester.put()
                .uri("/api/transactions/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
                .exchange();

        result.assertThat().matches(status().isOk());
        assertThat(result.getResponse().getContentAsString()).contains("Updated salary");
    }

    @Test
    void shouldDeleteTransaction() throws Exception {
        var result = mockMvcTester.delete()
                .uri("/api/transactions/1")
                .exchange();

        result.assertThat().matches(status().isNoContent());
    }

    @Test
    void shouldFindTransactionsByAccount() throws Exception {
        var response = new TransactionResponse(1L, 10L, new BigDecimal("50.00"),
                "DEPOSIT", "Salary", "ref-1", LocalDateTime.now());
        given(transactionService.findByAccountId(10L)).willReturn(List.of(response));

        var result = mockMvcTester.get()
                .uri("/api/transactions/by-account/10")
                .exchange();

        result.assertThat().matches(status().isOk());
        assertThat(result.getResponse().getContentAsString()).contains("\"accountId\":10");
    }
}
