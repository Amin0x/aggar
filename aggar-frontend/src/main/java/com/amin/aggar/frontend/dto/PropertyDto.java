package com.amin.aggar.frontend.dto;

import java.math.BigDecimal;
import java.util.List;

public class PropertyDto {
    private Long id;
    private String title;
    private String slug;
    private String description;
    private Long viewCount;
    private String listingType;
    private String category;
    private BigDecimal price;
    private String currency;
    private String pricePeriod;
    private Integer bedrooms;
    private Integer bathrooms;
    private Integer rooms;
    private Integer floors;
    private BigDecimal area;
    private Integer stateId;
    private Integer cityId;
    private Integer neighborhoodId;
    private Integer ownerId;
    private Integer agentId;
    private String status;
    private Double locationLat;
    private Double locationLng;
    private List<PropertyImageDto> images;
    private List<AmenityDto> amenities;

    // Contact info for owner/agent
    private String ownerName;
    private String ownerPhone;
    private String agentName;
    private String agentPhone;

    // For form display - temporary fields to hold names before conversion to IDs
    private String state;
    private String city;
    private String neighborhood;

    public PropertyDto() {}

    public static class PropertyImageDto {
        private Long id;
        private Long propertyId;
        private String url;
        private Boolean isPrimary;
        private Integer sortOrder;
        public PropertyImageDto() {}
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public Long getPropertyId() { return propertyId; }
        public void setPropertyId(Long propertyId) { this.propertyId = propertyId; }
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public Boolean getIsPrimary() { return isPrimary; }
        public void setIsPrimary(Boolean isPrimary) { this.isPrimary = isPrimary; }
        public Integer getSortOrder() { return sortOrder; }
        public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getSlug() { return slug; }
    public void setSlug(String slug) { this.slug = slug; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getViewCount() { return viewCount; }
    public void setViewCount(Long viewCount) { this.viewCount = viewCount; }
    public String getListingType() { return listingType; }
    public void setListingType(String listingType) { this.listingType = listingType; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
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
    public Integer getOwnerId() { return ownerId; }
    public void setOwnerId(Integer ownerId) { this.ownerId = ownerId; }
    public Integer getAgentId() { return agentId; }
    public void setAgentId(Integer agentId) { this.agentId = agentId; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Double getLocationLat() { return locationLat; }
    public void setLocationLat(Double locationLat) { this.locationLat = locationLat; }
    public Double getLocationLng() { return locationLng; }
    public void setLocationLng(Double locationLng) { this.locationLng = locationLng; }
    public List<PropertyImageDto> getImages() { return images; }
    public void setImages(List<PropertyImageDto> images) { this.images = images; }
    public List<AmenityDto> getAmenities() { return amenities; }
    public void setAmenities(List<AmenityDto> amenities) { this.amenities = amenities; }

    // Contact info getters and setters
    public String getOwnerName() { return ownerName; }
    public void setOwnerName(String ownerName) { this.ownerName = ownerName; }
    public String getOwnerPhone() { return ownerPhone; }
    public void setOwnerPhone(String ownerPhone) { this.ownerPhone = ownerPhone; }
    public String getAgentName() { return agentName; }
    public void setAgentName(String agentName) { this.agentName = agentName; }
    public String getAgentPhone() { return agentPhone; }
    public void setAgentPhone(String agentPhone) { this.agentPhone = agentPhone; }

    // Temporary form fields
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    public String getCity() { return city; }
    public void setCity(String city) { this.city = city; }
    public String getNeighborhood() { return neighborhood; }
    public void setNeighborhood(String neighborhood) { this.neighborhood = neighborhood; }
}
