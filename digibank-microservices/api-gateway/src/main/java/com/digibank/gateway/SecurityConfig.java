package com.digibank.gateway;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/actuator/health", "/actuator/health/**").permitAll()
                        .requestMatchers("/api/compliance/**", "/api/notifications/**").hasRole("ADMIN")
                        .requestMatchers("/api/**").hasAnyRole("USER", "ADMIN")
                        .anyRequest().authenticated())
                .httpBasic(basic -> {})
                .headers(headers -> headers
                        .frameOptions(frame -> frame.deny())
                        .contentTypeOptions(content -> {}));
        return http.build();
    }

    @Bean
    InMemoryUserDetailsManager users(
            @Value("${security.users.user.username:user}") String userName,
            @Value("${security.users.user.password:changeit-user}") String userPassword,
            @Value("${security.users.admin.username:admin}") String adminName,
            @Value("${security.users.admin.password:changeit-admin}") String adminPassword) {
        UserDetails user = User.withUsername(userName)
                .password("{noop}" + userPassword)
                .roles("USER")
                .build();
        UserDetails admin = User.withUsername(adminName)
                .password("{noop}" + adminPassword)
                .roles("USER", "ADMIN")
                .build();
        return new InMemoryUserDetailsManager(user, admin);
    }
}
