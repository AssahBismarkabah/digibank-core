package com.digibank.transaction.client;

import com.digibank.transaction.pattern.CircuitBreaker;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.time.Duration;

@Component
public class NotificationClient {
    private final RestClient client;
    private final CircuitBreaker circuitBreaker = new CircuitBreaker(3, Duration.ofSeconds(30));
    public NotificationClient(RestClient.Builder builder, @Value("${clients.notification-service-url:http://localhost:8085}") String baseUrl) { this.client = builder.baseUrl(baseUrl).build(); }
    public void notify(String reference) {
        circuitBreaker.execute(() -> { client.post().uri("/api/notifications").body(new NotificationRequest("transaction-operations", "Transaction " + reference + " accepted")).retrieve().toBodilessEntity(); return null; }, () -> null);
    }
    private record NotificationRequest(String recipient, String message) { }
}
