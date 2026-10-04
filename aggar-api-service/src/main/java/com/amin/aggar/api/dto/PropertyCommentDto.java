package com.amin.aggar.api.dto;

import java.time.LocalDateTime;

public record PropertyCommentDto(
        Long id,
        Long propertyId,
        Long authorId,
        String authorName,
        String content,
        LocalDateTime createdAt
) {
}
