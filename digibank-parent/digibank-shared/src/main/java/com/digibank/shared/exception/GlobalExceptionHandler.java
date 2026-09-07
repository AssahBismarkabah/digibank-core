package com.digibank.shared.exception;

import com.digibank.shared.dto.ApiError;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiError> handleEntityNotFound(EntityNotFoundException ex) {
        log.debug("Resource lookup failed", ex);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiError.of("Resource not found", List.of("The requested resource does not exist")));
    }

    @ExceptionHandler({NoHandlerFoundException.class, NoResourceFoundException.class})
    public ResponseEntity<ApiError> handleNoRouteFound(Exception ex) {
        log.debug("No route found for request", ex);
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(ApiError.of("Resource not found", List.of("The requested resource does not exist")));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        log.debug("Request validation failed: {} field error(s)", ex.getBindingResult().getErrorCount());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiError.of("Input validation failed", validationDetails(ex)));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex) {
        log.debug("Request constraint validation failed: {} violation(s)", ex.getConstraintViolations().size());
        List<String> details = ex.getConstraintViolations().stream()
                .map(this::constraintFieldName)
                .distinct()
                .sorted()
                .map(field -> "Invalid field: " + field)
                .toList();
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiError.of("Input validation failed", nonEmpty(details)));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadableMessage(HttpMessageNotReadableException ex) {
        log.debug("Request body could not be read", ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiError.of("Invalid request body", List.of("The request body could not be processed")));
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class, MissingServletRequestParameterException.class})
    public ResponseEntity<ApiError> handleInvalidRequestParameter(Exception ex) {
        log.debug("Request parameter validation failed", ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiError.of("Input validation failed", List.of("One or more request parameters are invalid")));
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex) {
        log.debug("HTTP method is not supported for request", ex);
        return ResponseEntity
                .status(HttpStatus.METHOD_NOT_ALLOWED)
                .body(ApiError.of("Request method not allowed", List.of("The requested operation is not supported")));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgument(IllegalArgumentException ex) {
        log.info("Business rule rejected request");
        log.debug("Business rule rejection detail", ex);
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(ApiError.of("Request could not be processed", List.of("Business rule validation failed")));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneral(Exception ex) {
        log.error("Unhandled application error", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiError.of("Internal server error", List.of("The operation could not be completed")));
    }

    private List<String> validationDetails(MethodArgumentNotValidException ex) {
        List<String> details = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getField)
                .distinct()
                .sorted()
                .map(field -> "Invalid field: " + field)
                .toList();
        return nonEmpty(details);
    }

    private String constraintFieldName(ConstraintViolation<?> violation) {
        String path = violation.getPropertyPath().toString();
        int separator = path.lastIndexOf('.');
        if (separator >= 0 && separator < path.length() - 1) {
            return path.substring(separator + 1);
        }
        return path;
    }

    private List<String> nonEmpty(List<String> details) {
        if (details.isEmpty()) {
            return List.of("One or more fields are invalid");
        }
        return details;
    }
}
