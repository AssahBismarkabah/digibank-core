package com.digibank.gateway;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class GatewayRootController {
    @GetMapping("/")
    public Map<String, String> index() {
        return Map.of("service", "api-gateway", "status", "UP");
    }
}
