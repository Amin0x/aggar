package com.amin.aggar.domain.entity;

import com.amin.aggar.domain.enums.ListingType;
import com.amin.aggar.domain.enums.PricePeriod;
import com.amin.aggar.domain.enums.ListingTypeConverter;
import com.amin.aggar.domain.enums.PricePeriodConverter;
import jakarta.persistence.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "properties",
       indexes = {
               @Index(name = "idx_properties_listing_city_price", columnList = "listing_type,city_id,price"),
               @Index(name = "idx_properties_city_bedrooms_price", columnList = "city_id,bedrooms,price"),
               @Index(name = "idx_properties_status_published", columnList = "status,published_at")
       })
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Convert(converter = ListingTypeConverter.class)
    @Column(name = "listing_type", nullable = false, length = 10)
    private ListingType listingType;

    @Column(nullable = false, length = 255)
    private String title;

    @Lob
    private String description;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal price;

    @Column(nullable = false, length = 3)
    @ColumnDefault("'USD'")
    private String currency;

    @Convert(converter = PricePeriodConverter.class)
    @Column(name = "price_period", length = 20)
    private PricePeriod pricePeriod;

    private Integer bedrooms;
    private Integer bathrooms;
    private Integer rooms;
    private Integer floors;

    @Column(precision = 10, scale = 2)
    private BigDecimal area;

    @ManyToOne(optional = false)
    @JoinColumn(name = "state_id", nullable = false)
    private State state;

    @ManyToOne(optional = false)
    @JoinColumn(name = "city_id", nullable = false)
    private City city;

    @ManyToOne
    @JoinColumn(name = "neighborhood_id")
    private Neighborhood neighborhood;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private User owner;

    @ManyToOne
    @JoinColumn(name = "agent_id")
    private User agent;

    @Column(length = 30)
    @ColumnDefault("'available'")
    private String status;

    @Column(name = "location_lat")
    private Double locationLat;

    @Column(name = "location_lng")
    private Double locationLng;

    // Note: skipping direct mapping of POINT column; use lat/lng instead

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PropertyImage> images;

    @ManyToMany
    @JoinTable(name = "property_amenities",
            joinColumns = @JoinColumn(name = "property_id"),
            inverseJoinColumns = @JoinColumn(name = "amenity_id"))
    private Set<Amenity> amenities;

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL)
    private List<PriceHistory> priceHistory;

    public Property() {}

    // getters and setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ListingType getListingType() { return listingType; }
    public void setListingType(ListingType listingType) { this.listingType = listingType; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }
    public PricePeriod getPricePeriod() { return pricePeriod; }
    public void setPricePeriod(PricePeriod pricePeriod) { this.pricePeriod = pricePeriod; }
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
    public State getState() { return state; }
    public void setState(State state) { this.state = state; }
    public City getCity() { return city; }
    public void setCity(City city) { this.city = city; }
    public Neighborhood getNeighborhood() { return neighborhood; }
    public void setNeighborhood(Neighborhood neighborhood) { this.neighborhood = neighborhood; }
    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }
    public User getAgent() { return agent; }
    public void setAgent(User agent) { this.agent = agent; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Double getLocationLat() { return locationLat; }
    public void setLocationLat(Double locationLat) { this.locationLat = locationLat; }
    public Double getLocationLng() { return locationLng; }
    public void setLocationLng(Double locationLng) { this.locationLng = locationLng; }
    public LocalDateTime getPublishedAt() { return publishedAt; }
    public void setPublishedAt(LocalDateTime publishedAt) { this.publishedAt = publishedAt; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }
    public List<PropertyImage> getImages() { return images; }
    public void setImages(List<PropertyImage> images) { this.images = images; }
    public Set<Amenity> getAmenities() { return amenities; }
    public void setAmenities(Set<Amenity> amenities) { this.amenities = amenities; }
    public List<PriceHistory> getPriceHistory() { return priceHistory; }
    public void setPriceHistory(List<PriceHistory> priceHistory) { this.priceHistory = priceHistory; }
}
