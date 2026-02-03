package com.rec_notification.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailNotificationRequest {
    @NotBlank(message = "Recipient is required")
    @Email(message = "Invalid email format")
    private String recipient;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Content is required")
    private String content;

    private List<MultipartFile> attachments;
}