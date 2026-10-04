package com.amin.aggar.frontend.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PriceHistoryDto(
        Long id,
        Long propertyId,
        BigDecimal oldPrice,
        BigDecimal newPrice,
        Long changedById,
        LocalDateTime changedAt
) {
}
