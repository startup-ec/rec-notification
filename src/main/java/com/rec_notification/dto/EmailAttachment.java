package com.rec_notification.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailAttachment {
    private Long id;
    private String fileName;
    private String contentType;
    private byte[] data;
}