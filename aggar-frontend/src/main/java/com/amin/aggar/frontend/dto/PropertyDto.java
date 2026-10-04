package com.amin.aggar.frontend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

public record PropertyDto(
        Long id,
        String title,
        String slug,
        String description,
        BigDecimal price,
        Long viewCount,
        String currency,
        String listingType,
        String category,
        String pricePeriod,
        Integer bedrooms,
        Integer bathrooms,
        BigDecimal area,
        Integer stateId,
        Integer cityId,
        Integer neighborhoodId,
        Long ownerId,
        Long agentId,
        String status,
        Double locationLat,
        Double locationLng,
        LocalDateTime publishedAt,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Boolean isDeleted,
        List<PropertyImageDto> images,
        Set<AmenityDto> amenities,
        List<PriceHistoryDto> priceHistory,
        String ownerName,
        String ownerPhone,
        String agentName,
        String agentPhone,
        Integer rooms,
        Integer floors,
        String state,
        String city,
        String neighborhood
) {
}
