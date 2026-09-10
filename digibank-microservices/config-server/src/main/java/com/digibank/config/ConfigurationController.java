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
        return Map.of("service", serviceName, "profile", profile, "source", "config-server");
    }
}
