package com.digibank.notification.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {
    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public NotificationResponse send(@Valid @RequestBody NotificationRequest request) {
        return new NotificationResponse("ACCEPTED", request.recipient(), request.message());
    }

    public record NotificationRequest(@NotBlank String recipient, @NotBlank String message) { }
    public record NotificationResponse(String status, String recipient, String message) { }
}
