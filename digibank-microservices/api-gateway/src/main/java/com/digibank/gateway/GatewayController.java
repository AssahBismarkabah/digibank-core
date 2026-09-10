package com.digibank.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

@RestController
@RequestMapping("/api")
public class GatewayController {
    private final RestClient.Builder builder;
    private final String customerUrl;
    private final String accountUrl;
    private final String transactionUrl;
    private final String complianceUrl;
    private final String notificationUrl;

    public GatewayController(
            @Value("${services.customer-url}") String customerUrl,
            @Value("${services.account-url}") String accountUrl,
            @Value("${services.transaction-url}") String transactionUrl,
            @Value("${services.compliance-url}") String complianceUrl,
            @Value("${services.notification-url}") String notificationUrl) {
        this.builder = RestClient.builder();
        this.customerUrl = customerUrl;
        this.accountUrl = accountUrl;
        this.transactionUrl = transactionUrl;
        this.complianceUrl = complianceUrl;
        this.notificationUrl = notificationUrl;
    }

    @GetMapping("/customers{path:.*}")
    public ResponseEntity<Object> customerRead(@PathVariable String path) {
        return forward(customerUrl, "/api/customers" + path, HttpMethod.valueOf("GET"));
    }

    @PostMapping("/customers")
    public ResponseEntity<Object> createCustomer(@RequestBody Object body) {
        return post(customerUrl, "/api/customers", body);
    }

    @GetMapping("/accounts{path:.*}")
    public ResponseEntity<Object> accountRead(@PathVariable String path) {
        return forward(accountUrl, "/api/accounts" + path, HttpMethod.GET);
    }

    @PostMapping("/accounts")
    public ResponseEntity<Object> createAccount(@RequestBody Object body) {
        return post(accountUrl, "/api/accounts", body);
    }

    @GetMapping("/transactions{path:.*}")
    public ResponseEntity<Object> transactionRead(@PathVariable String path) {
        return forward(transactionUrl, "/api/transactions" + path, HttpMethod.GET);
    }

    @PostMapping("/transactions")
    public ResponseEntity<Object> createTransaction(@RequestBody Object body) {
        return post(transactionUrl, "/api/transactions", body);
    }

    @GetMapping("/compliance{path:.*}")
    public ResponseEntity<Object> complianceRead(@PathVariable String path) {
        return forward(complianceUrl, "/api/compliance" + path, HttpMethod.GET);
    }

    @PostMapping("/compliance")
    public ResponseEntity<Object> createCompliance(@RequestBody Object body) {
        return post(complianceUrl, "/api/compliance", body);
    }

    @PostMapping("/notifications")
    public ResponseEntity<Object> notify(@RequestBody Object body) {
        return post(notificationUrl, "/api/notifications", body);
    }

    private ResponseEntity<Object> post(String baseUrl, String path, Object body) {
        return builder.baseUrl(baseUrl).build().post().uri(path).body(body).retrieve().toEntity(Object.class);
    }

    private ResponseEntity<Object> forward(String baseUrl, String path, HttpMethod method) {
        return builder.baseUrl(baseUrl).build().method(method).uri(path).retrieve().toEntity(Object.class);
    }
}
