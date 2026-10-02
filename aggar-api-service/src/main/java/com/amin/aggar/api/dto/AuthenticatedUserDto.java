package com.amin.aggar.api.dto;

import java.time.LocalDateTime;

public record AuthenticatedUserDto(
        Long id,
        String username,
        String name,
        String email,
        String phone,
        String role,
        LocalDateTime createdAt
) {
}
