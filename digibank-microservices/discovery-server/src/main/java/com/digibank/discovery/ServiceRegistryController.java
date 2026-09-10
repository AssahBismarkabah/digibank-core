package com.digibank.discovery;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
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
        String url = services.get(serviceName);
        if (url == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Service is not registered");
        }
        return url;
    }
}
