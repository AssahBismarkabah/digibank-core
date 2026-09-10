package com.digibank.transaction.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.math.BigDecimal;

@Component
public class AccountClient {
    private final RestClient client;
    public AccountClient(@Value("${clients.account-service-url:http://localhost:8082}") String baseUrl) { this.client = RestClient.builder().baseUrl(baseUrl).build(); }
    public void updateBalance(Long accountId, BigDecimal amount, String operation) {
        client.post().uri("/api/accounts/internal/{id}/balance", accountId).body(new BalanceRequest(amount, operation)).retrieve().toBodilessEntity();
    }
    public void compensateBalance(Long accountId, BigDecimal amount, String operation) {
        client.post().uri("/api/accounts/internal/{id}/balance/compensate", accountId).body(new BalanceRequest(amount, operation)).retrieve().toBodilessEntity();
    }
    private record BalanceRequest(BigDecimal amount, String operation) { }
}
