package com.amin.aggar.api.dto;

import java.time.LocalDateTime;

public record MessageDto(
        Long id,
        Long propertyId,
        Long senderId,
        String senderName,
        String subject,
        String content,
        Boolean isRead,
        LocalDateTime createdAt
) {
}
