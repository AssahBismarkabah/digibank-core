package com.digibank.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestClient;

import java.util.Map;

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

    @GetMapping({"/customers", "/customers/{id}"})
    public ResponseEntity<Object> customerRead(@PathVariable(required = false) String id) {
        return forward(customerUrl, "/api/customers" + suffix(id), HttpMethod.GET);
    }

    @PostMapping("/customers")
    public ResponseEntity<Object> createCustomer(@RequestBody Object body) {
        return post(customerUrl, "/api/customers", body);
    }

    @GetMapping({"/accounts", "/accounts/{id}", "/accounts/by-customer/{customerId}"})
    public ResponseEntity<Object> accountRead(@PathVariable Map<String, String> variables) {
        String path = variables.containsKey("customerId") ? "/by-customer/" + variables.get("customerId") : suffix(variables.get("id"));
        return forward(accountUrl, "/api/accounts" + path, HttpMethod.GET);
    }

    @PostMapping("/accounts")
    public ResponseEntity<Object> createAccount(@RequestBody Object body) {
        return post(accountUrl, "/api/accounts", body);
    }

    @GetMapping({"/transactions", "/transactions/{id}", "/transactions/by-account/{accountId}"})
    public ResponseEntity<Object> transactionRead(@PathVariable Map<String, String> variables) {
        String path = variables.containsKey("accountId") ? "/by-account/" + variables.get("accountId") : suffix(variables.get("id"));
        return forward(transactionUrl, "/api/transactions" + path, HttpMethod.GET);
    }

    @PostMapping("/transactions")
    public ResponseEntity<Object> createTransaction(@RequestBody Object body) {
        return post(transactionUrl, "/api/transactions", body);
    }

    @GetMapping({"/compliance", "/compliance/{id}", "/compliance/by-customer/{customerId}"})
    public ResponseEntity<Object> complianceRead(@PathVariable Map<String, String> variables) {
        String path = variables.containsKey("customerId") ? "/by-customer/" + variables.get("customerId") : suffix(variables.get("id"));
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
        return relay(builder.baseUrl(baseUrl).build().post().uri(path).body(body).retrieve().toEntity(Object.class));
    }

    private ResponseEntity<Object> forward(String baseUrl, String path, HttpMethod method) {
        return relay(builder.baseUrl(baseUrl).build().method(method).uri(path).retrieve().toEntity(Object.class));
    }

    private ResponseEntity<Object> relay(ResponseEntity<Object> response) {
        return ResponseEntity.status(response.getStatusCode()).body(response.getBody());
    }

    private String suffix(String id) {
        return id == null ? "" : "/" + id;
    }
}
