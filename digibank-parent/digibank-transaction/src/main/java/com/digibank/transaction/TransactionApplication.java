package com.digibank.transaction;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.digibank",
        excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.digibank\\..*Application"))
@EnableJpaRepositories(basePackages = "com.digibank")
@EntityScan(basePackages = "com.digibank")
public class TransactionApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransactionApplication.class, args);
    }
}
