package com.rec_notification.service;

import com.rec_notification.dto.*;
import com.rec_notification.enums.*;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${notification.from-email}")
    private String fromEmail;

    @Value("${notification.from-name}")
    private String fromName;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Async
    public CompletableFuture<NotificationResponse> sendEmail(EmailNotificationRequest request) {
        log.info("Sending email to: {}", request.getRecipient());
        NotificationResponse response = new NotificationResponse();
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, fromName);
            helper.setTo(request.getRecipient());
            helper.setSubject(request.getSubject());
            helper.setText(request.getContent(), true);

            // Procesar archivos adjuntos si existen
            if (request.getAttachments() != null && !request.getAttachments().isEmpty()) {
                processAttachments(request.getAttachments(), helper);
            }
            mailSender.send(message);
            log.info("Email sent successfully to: {}", request.getRecipient());
        } catch (Exception e) {
            log.error("Failed to send email to: {}", request.getRecipient(), e);
            response.setStatus(NotificationStatus.FAILED);
        }
        response.setStatus(NotificationStatus.SENT);
        return CompletableFuture.completedFuture(response);
    }

    private void processAttachments(List<MultipartFile> files, MimeMessageHelper helper) throws Exception {
        List<EmailAttachment> attachments = new ArrayList<>();

        for (MultipartFile file : files) {
            if (!file.isEmpty()) {
                validateFile(file);
                // Agregar al correo
                helper.addAttachment(file.getOriginalFilename(), file);
                EmailAttachment attachment = EmailAttachment.builder()
                        .fileName(file.getOriginalFilename())
                        .contentType(file.getContentType())
                        .data(file.getBytes())
                        .build();
                attachments.add(attachment);
            }
        }
    }

    private void validateFile(MultipartFile file) throws Exception {
        // Validar tamaño (máximo 5MB por archivo)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("File size exceeds 5MB limit: " + file.getOriginalFilename());
        }

        // Validar tipos de archivo permitidos
        String contentType = file.getContentType();
        List<String> allowedTypes = Arrays.asList(
                "application/pdf",
                "application/msword",
                "application/vnd.openxmlformats-officedocument.wordprocessingml.document",
                "application/vnd.ms-excel",
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
                "image/jpeg",
                "image/png",
                "text/plain"
        );

        if (!allowedTypes.contains(contentType)) {
            throw new IllegalArgumentException("File type not allowed: " + contentType);
        }
    }
}