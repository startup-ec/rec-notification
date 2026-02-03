package com.rec_notification.controller;

import com.rec_notification.dto.*;
import com.rec_notification.enums.*;
import com.rec_notification.service.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/notifications")
@Validated
@Slf4j
public class NotificationController {

    private final EmailService emailService;

    public NotificationController(EmailService emailService) {
        this.emailService = emailService;
    }

    @PostMapping("/email")
    public ResponseEntity<NotificationResponse> sendEmail(@Valid @RequestBody EmailNotificationRequest request) {
        try {
            CompletableFuture<NotificationResponse> future = emailService.sendEmail(request);
            NotificationResponse response = future.get(30, TimeUnit.SECONDS);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error sending email", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(NotificationResponse.builder()
                            .status(NotificationStatus.FAILED)
                            .errorMessage(e.getMessage())
                            .build());
        }
    }

    @PostMapping("/email/with-attachments")
    public ResponseEntity<NotificationResponse> sendEmailWithAttachments(
            @RequestParam("recipient") @Email String recipient,
            @RequestParam("subject") String subject,
            @RequestParam("content") String content,
            @RequestParam("attachments") List<MultipartFile> attachments) {

        EmailNotificationRequest request = EmailNotificationRequest.builder()
                .recipient(recipient)
                .subject(subject)
                .content(content)
                .attachments(attachments)
                .build();

        try {
            CompletableFuture<NotificationResponse> future = emailService.sendEmail(request);
            NotificationResponse response = future.get(30, TimeUnit.SECONDS);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error sending email with attachments", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(NotificationResponse.builder()
                            .status(NotificationStatus.FAILED)
                            .errorMessage(e.getMessage())
                            .build());
        }
    }
}