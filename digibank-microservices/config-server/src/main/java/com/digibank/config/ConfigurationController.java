package com.digibank.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/config")
public class ConfigurationController {
    private final String profile;

    public ConfigurationController(@Value("${spring.profiles.active:default}") String profile) {
        this.profile = profile;
    }

    @GetMapping("/{serviceName}")
    public Map<String, Object> configuration(@PathVariable String serviceName) {
        Map<String, Object> values = switch (serviceName) {
            case "api-gateway" -> Map.of(
                    "server.port", 8080,
                    "services.customer-url", "http://customer-service:8081",
                    "services.account-url", "http://account-service:8082");
            case "transaction-service" -> Map.of(
                    "server.port", 8083,
                    "clients.account-service-url", "http://account-service:8082",
                    "clients.notification-service-url", "http://notification-service:8085");
            default -> Map.of("server.port", 0);
        };
        return Map.of("service", serviceName, "profile", profile, "source", "config-server", "properties", values);
    }
}
