package com.amin.aggar.api.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PriceHistoryDto {
    private Long id;
    private Long propertyId;
    private BigDecimal oldPrice;
    private BigDecimal newPrice;
    private Integer changedById;
    private LocalDateTime changedAt;

    public PriceHistoryDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getPropertyId() { return propertyId; }
    public void setPropertyId(Long propertyId) { this.propertyId = propertyId; }
    public BigDecimal getOldPrice() { return oldPrice; }
    public void setOldPrice(BigDecimal oldPrice) { this.oldPrice = oldPrice; }
    public BigDecimal getNewPrice() { return newPrice; }
    public void setNewPrice(BigDecimal newPrice) { this.newPrice = newPrice; }
    public Integer getChangedById() { return changedById; }
    public void setChangedById(Integer changedById) { this.changedById = changedById; }
    public LocalDateTime getChangedAt() { return changedAt; }
    public void setChangedAt(LocalDateTime changedAt) { this.changedAt = changedAt; }
}

