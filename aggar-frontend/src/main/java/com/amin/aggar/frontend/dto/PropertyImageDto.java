package com.amin.aggar.frontend.dto;

public record PropertyImageDto(
        Long id,
        Long propertyId,
        String url,
        Boolean isPrimary,
        Integer sortOrder
) {
}
