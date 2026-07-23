package com.digibank.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.digibank")
@EnableJpaRepositories(basePackages = "com.digibank")
@EntityScan(basePackages = "com.digibank")
public class DigiBankApplication extends SpringBootServletInitializer {

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
        return builder.sources(DigiBankApplication.class);
    }

    public static void main(String[] args) {
        SpringApplication.run(DigiBankApplication.class, args);
    }
}
