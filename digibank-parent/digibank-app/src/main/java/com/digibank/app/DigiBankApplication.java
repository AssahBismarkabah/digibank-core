package com.digibank.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = "com.digibank",
        excludeFilters = @ComponentScan.Filter(type = FilterType.REGEX, pattern = "com\\.digibank\\..*Application"))
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
