package com.digibank.discovery;

import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ServiceRegistryControllerTest {

    @Test
    void registersAndResolvesService() {
        ServiceRegistryController registry = new ServiceRegistryController();

        registry.register("customer-service", "http://customer-service:8081");

        assertThat(registry.find("customer-service")).isEqualTo("http://customer-service:8081");
        assertThat(registry.list()).containsEntry("customer-service", "http://customer-service:8081");
    }

    @Test
    void unknownServiceReturnsNotFound() {
        ServiceRegistryController registry = new ServiceRegistryController();

        assertThatThrownBy(() -> registry.find("missing-service"))
                .isInstanceOfSatisfying(ResponseStatusException.class,
                        exception -> assertThat(exception.getStatusCode().value()).isEqualTo(404));
    }
}
