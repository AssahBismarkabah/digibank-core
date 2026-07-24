package com.digibank.compliance;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.digibank.compliance")
public class TestDigiBankComplianceApplication {
    // Minimal Spring Boot configuration for testing the compliance module
}
