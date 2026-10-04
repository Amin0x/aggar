package com.amin.aggar.api.dto;

public record PropertyImageDto(
        Long id,
        Long propertyId,
        String url,
        Boolean isPrimary,
        Integer sortOrder
) {
}

