package com.digibank.shared.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.LocalDateTime;
import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

    private final boolean success;
    private final String message;
    private final List<String> details;
    private final LocalDateTime timestamp;

    public ApiError() {
        this.success = false;
        this.message = null;
        this.details = null;
        this.timestamp = LocalDateTime.now();
    }

    public ApiError(String message, List<String> details) {
        this.success = false;
        this.message = message;
        this.details = List.copyOf(details);
        this.timestamp = LocalDateTime.now();
    }

    public static ApiError of(String message, List<String> details) {
        return new ApiError(message, details);
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getDetails() {
        return details;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}
