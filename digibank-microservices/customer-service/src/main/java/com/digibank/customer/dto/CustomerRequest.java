package com.digibank.customer.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CustomerRequest(
        @NotBlank(message = "First name is required")
        @Size(max = 50, message = "First name must not exceed 50 characters")
        @Pattern(regexp = "^[\\p{L}][\\p{L} .'-]*$", message = "First name contains invalid characters")
        String firstName,
        @NotBlank(message = "Last name is required")
        @Size(max = 50, message = "Last name must not exceed 50 characters")
        @Pattern(regexp = "^[\\p{L}][\\p{L} .'-]*$", message = "Last name contains invalid characters")
        String lastName,
        @NotBlank(message = "Email is required")
        @Size(max = 100, message = "Email must not exceed 100 characters")
        @Email(message = "Email must be a valid format")
        String email) { }
