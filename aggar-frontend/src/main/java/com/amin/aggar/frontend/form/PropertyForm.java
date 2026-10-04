package com.amin.aggar.frontend.form;

import com.amin.aggar.frontend.dto.PropertyDto;

import java.math.BigDecimal;

public class PropertyForm {
    private String title;
    private String description;
    private BigDecimal price;
    private String currency;
    private String listingType;
    private String category;
    private String pricePeriod;
    private Integer bedrooms;
    private Integer bathrooms;
    private Integer rooms;
    private Integer floors;
    private BigDecimal area;
    private Integer stateId;
    private Integer cityId;
    private Integer neighborhoodId;
    private String status;
    private Double locationLat;
    private Double locationLng;
    private String neighborhood;

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public String getListingType() { return listingType; }
    public void setListingType(String listingType) { this.listingType = listingType; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getPricePeriod() { return pricePeriod; }
    public void setPricePeriod(String pricePeriod) { this.pricePeriod = pricePeriod; }
    public Integer getBedrooms() { return bedrooms; }
    public void setBedrooms(Integer bedrooms) { this.bedrooms = bedrooms; }
    public Integer getBathrooms() { return bathrooms; }
    public void setBathrooms(Integer bathrooms) { this.bathrooms = bathrooms; }
    public Integer getRooms() { return rooms; }
    public void setRooms(Integer rooms) { this.rooms = rooms; }
    public Integer getFloors() { return floors; }
    public void setFloors(Integer floors) { this.floors = floors; }
    public BigDecimal getArea() { return area; }
    public void setArea(BigDecimal area) { this.area = area; }
    public Integer getStateId() { return stateId; }
    public void setStateId(Integer stateId) { this.stateId = stateId; }
    public Integer getCityId() { return cityId; }
    public void setCityId(Integer cityId) { this.cityId = cityId; }
    public Integer getNeighborhoodId() { return neighborhoodId; }
    public void setNeighborhoodId(Integer neighborhoodId) { this.neighborhoodId = neighborhoodId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Double getLocationLat() { return locationLat; }
    public void setLocationLat(Double locationLat) { this.locationLat = locationLat; }
    public Double getLocationLng() { return locationLng; }
    public void setLocationLng(Double locationLng) { this.locationLng = locationLng; }
    public String getNeighborhood() { return neighborhood; }
    public void setNeighborhood(String neighborhood) { this.neighborhood = neighborhood; }

    public PropertyDto toDto() {
        return new PropertyDto(null, title, null, description, price, null, currency, listingType, category,
                pricePeriod, bedrooms, bathrooms, area, stateId, cityId, neighborhoodId, null, null, status,
                locationLat, locationLng, null, null, null, null, null, null, null, null, null, null, null,
                rooms, floors, null, null, neighborhood);
    }
}
