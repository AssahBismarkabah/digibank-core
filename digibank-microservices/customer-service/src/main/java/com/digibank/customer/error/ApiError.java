package com.digibank.customer.error;

import java.time.LocalDateTime;
import java.util.List;

public record ApiError(boolean success, String message, List<String> details, LocalDateTime timestamp) {
    public static ApiError of(String message, String detail) {
        return new ApiError(false, message, List.of(detail), LocalDateTime.now());
    }
}
