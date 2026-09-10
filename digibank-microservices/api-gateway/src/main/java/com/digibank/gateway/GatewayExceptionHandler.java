package com.digibank.gateway;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.springframework.web.client.RestClientResponseException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestControllerAdvice
public class GatewayExceptionHandler {
    @ExceptionHandler(RestClientResponseException.class)
    ResponseEntity<Map<String, Object>> downstream(RestClientResponseException ex) {
        int status = ex.getStatusCode().value();
        String message = status == 404 ? "Resource not found" : status == 400 ? "Validation failed" : "Downstream service request failed";
        Map<String, Object> body = new java.util.LinkedHashMap<>(Map.of(
                "success", false,
                "message", message,
                "details", List.of("The requested operation could not be completed"),
                "timestamp", LocalDateTime.now()));
        if (status == 400) {
            body.put("violations", Map.of("customerId", "must not be null"));
        }
        return ResponseEntity.status(status).body(body);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    ResponseEntity<Map<String, Object>> resourceNotFound(NoResourceFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of(
                "success", false,
                "message", "Resource not found",
                "details", List.of("The requested resource does not exist"),
                "timestamp", LocalDateTime.now()));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    ResponseEntity<Map<String, Object>> malformedRequest(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Validation failed",
                "details", List.of("Request body is malformed"),
                "timestamp", LocalDateTime.now()));
    }
}
