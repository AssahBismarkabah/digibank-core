package com.digibank.customer;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.digibank.customer")
public class TestDigiBankCustomerApplication {
    // Minimal Spring Boot configuration for testing the customer module
}
