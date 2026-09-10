package com.digibank.transaction.error;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class) ResponseEntity<ApiError> notFound(EntityNotFoundException ex) { return response(HttpStatus.NOT_FOUND, "Resource not found", "The requested resource does not exist"); }
    @ExceptionHandler(MethodArgumentNotValidException.class) ResponseEntity<ApiError> validation(MethodArgumentNotValidException ex) { return response(HttpStatus.BAD_REQUEST, "Validation failed", "One or more fields are invalid"); }
    @ExceptionHandler(HttpMessageNotReadableException.class) ResponseEntity<ApiError> unreadable(HttpMessageNotReadableException ex) { return response(HttpStatus.BAD_REQUEST, "Invalid request body", "The request body could not be processed"); }
    @ExceptionHandler(IllegalArgumentException.class) ResponseEntity<ApiError> illegalArgument(IllegalArgumentException ex) { return response(HttpStatus.BAD_REQUEST, "Request could not be processed", "Business rule validation failed"); }
    @ExceptionHandler(Exception.class) ResponseEntity<ApiError> internal(Exception ex) { return response(HttpStatus.INTERNAL_SERVER_ERROR, "Internal server error", "The operation could not be completed"); }
    private ResponseEntity<ApiError> response(HttpStatus status, String message, String detail) { return ResponseEntity.status(status).body(ApiError.of(message, detail)); }
}
