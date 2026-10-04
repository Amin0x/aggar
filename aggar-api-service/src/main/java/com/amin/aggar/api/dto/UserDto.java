package com.amin.aggar.api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

public record UserDto(
        Long id,
        String username,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String password,
        String name,
        String email,
        String phone,
        String role,
        LocalDateTime createdAt
) {

}
