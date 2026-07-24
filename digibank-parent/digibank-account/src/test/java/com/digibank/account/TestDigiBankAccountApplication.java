package com.digibank.account;

import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@SpringBootConfiguration
@EnableAutoConfiguration
@ComponentScan(basePackages = "com.digibank.account")
public class TestDigiBankAccountApplication {
    // Minimal Spring Boot configuration for testing the account module
}
