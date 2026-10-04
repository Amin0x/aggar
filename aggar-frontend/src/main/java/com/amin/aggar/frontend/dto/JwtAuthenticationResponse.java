package com.amin.aggar.frontend.dto;

import java.time.Instant;
import java.time.LocalDateTime;

public record JwtAuthenticationResponse(
        Long id,
        String username,
        String name,
        String email,
        String phone,
        String role,
        LocalDateTime createdAt,
        String accessToken,
        String tokenType,
        Instant expiresAt
) {
}
