package com.digibank.discovery;

import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@RestController
@RequestMapping("/registry")
public class ServiceRegistryController {
    private final Map<String, String> services = new ConcurrentHashMap<>();

    @PutMapping("/{serviceName}")
    public void register(@PathVariable String serviceName, @RequestParam String url) {
        services.put(serviceName, url);
    }

    @GetMapping
    public Map<String, String> list() {
        return Map.copyOf(services);
    }

    @GetMapping("/{serviceName}")
    public String find(@PathVariable String serviceName) {
        return services.get(serviceName);
    }
}
