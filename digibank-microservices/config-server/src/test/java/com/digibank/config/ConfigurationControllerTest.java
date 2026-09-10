package com.digibank.config;

import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ConfigurationControllerTest {

    @Test
    void exposesGatewayProperties() {
        Map<String, Object> configuration = new ConfigurationController("dev").configuration("api-gateway");

        assertThat(configuration).containsEntry("service", "api-gateway");
        assertThat((Map<String, Object>) configuration.get("properties"))
                .containsEntry("server.port", 8080)
                .containsEntry("services.customer-url", "http://customer-service:8081");
    }

    @Test
    void exposesTransactionClientProperties() {
        Map<String, Object> configuration = new ConfigurationController("dev").configuration("transaction-service");

        assertThat((Map<String, Object>) configuration.get("properties"))
                .containsEntry("clients.account-service-url", "http://account-service:8082")
                .containsEntry("clients.notification-service-url", "http://notification-service:8085");
    }
}
