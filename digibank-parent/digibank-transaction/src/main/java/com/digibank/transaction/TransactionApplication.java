package com.digibank.transaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.digibank")
@EnableJpaRepositories(basePackages = "com.digibank")
@EntityScan(basePackages = "com.digibank")
public class TransactionApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(TransactionApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(TransactionApplication.class, args);
    }
}
