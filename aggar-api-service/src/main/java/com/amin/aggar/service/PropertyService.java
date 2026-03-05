package com.amin.aggar.service;

import com.amin.aggar.api.dto.*;
import com.amin.aggar.domain.entity.*;
import com.amin.aggar.domain.enums.ListingType;
import com.amin.aggar.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PropertyService {

    private final PropertyRepository propertyRepository;
    private final StateRepository stateRepository;
    private final CityRepository cityRepository;
    private final NeighborhoodRepository neighborhoodRepository;
    private final UserRepository userRepository;
    private final AmenityRepository amenityRepository;
    private final PropertyImageRepository propertyImageRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final EntityManager entityManager;

    public PropertyService(PropertyRepository propertyRepository,
                           StateRepository stateRepository,
                           CityRepository cityRepository,
                           NeighborhoodRepository neighborhoodRepository,
                           UserRepository userRepository,
                           AmenityRepository amenityRepository,
                           PropertyImageRepository propertyImageRepository,
                           PriceHistoryRepository priceHistoryRepository, EntityManager entityManager) {
        this.propertyRepository = propertyRepository;
        this.stateRepository = stateRepository;
        this.cityRepository = cityRepository;
        this.neighborhoodRepository = neighborhoodRepository;
        this.userRepository = userRepository;
        this.amenityRepository = amenityRepository;
        this.propertyImageRepository = propertyImageRepository;
        this.priceHistoryRepository = priceHistoryRepository;
        this.entityManager = entityManager;
    }

    private PropertyDto toDto(Property p) {
        if (p == null) return null;
        PropertyDto d = new PropertyDto();
        d.setId(p.getId());
        d.setTitle(p.getTitle());
        d.setDescription(p.getDescription());
        d.setPrice(p.getPrice());
        d.setCurrency(p.getCurrency());
        d.setListingType(p.getListingType() != null ? p.getListingType().getValue() : null);
        d.setPricePeriod(p.getPricePeriod()!=null? p.getPricePeriod().getValue() : null);
        d.setBedrooms(p.getBedrooms());
        d.setBathrooms(p.getBathrooms());
        d.setArea(p.getArea());
        d.setStateId(p.getState()!=null? p.getState().getId():null);
        d.setCityId(p.getCity()!=null? p.getCity().getId():null);
        d.setNeighborhoodId(p.getNeighborhood()!=null? p.getNeighborhood().getId():null);
        d.setOwnerId(p.getOwner()!=null? p.getOwner().getId():null);
        d.setAgentId(p.getAgent()!=null? p.getAgent().getId():null);
        d.setStatus(p.getStatus());
        d.setLocationLat(p.getLocationLat());
        d.setLocationLng(p.getLocationLng());
        d.setPublishedAt(p.getPublishedAt());
        d.setCreatedAt(p.getCreatedAt());
        d.setUpdatedAt(p.getUpdatedAt());
        d.setIsDeleted(p.getIsDeleted());
        // images
        if (p.getImages()!=null) {
            d.setImages(p.getImages().stream().map(img -> {
                PropertyImageDto idto = new PropertyImageDto();
                idto.setId(img.getId());
                idto.setPropertyId(p.getId());
                idto.setUrl(img.getUrl());
                idto.setIsPrimary(img.getIsPrimary());
                idto.setSortOrder(img.getSortOrder());
                return idto;
            }).collect(Collectors.toList()));
        }
        // amenities
        if (p.getAmenities()!=null) {
            d.setAmenities(p.getAmenities().stream().map(a -> { AmenityDto ad = new AmenityDto(); ad.setId(a.getId()); ad.setName(a.getName()); return ad; }).collect(Collectors.toSet()));
        }
        // price history omitted (can add similarly)
        return d;
    }

    private Property fromDto(PropertyDto d) {
        if (d == null) return null;
        Property p = new Property();
        p.setId(d.getId());
        p.setTitle(d.getTitle());
        p.setDescription(d.getDescription());
        p.setPrice(d.getPrice());
        p.setCurrency(d.getCurrency());
        if (d.getListingType()!=null) p.setListingType(ListingType.fromValue(d.getListingType()));
        // pricePeriod mapping omitted for brevity
        p.setBedrooms(d.getBedrooms());
        p.setBathrooms(d.getBathrooms());
        p.setArea(d.getArea());
        if (d.getStateId()!=null) p.setState(stateRepository.findById(d.getStateId()).orElse(null));
        if (d.getCityId()!=null) p.setCity(cityRepository.findById(d.getCityId()).orElse(null));
        if (d.getNeighborhoodId()!=null) p.setNeighborhood(neighborhoodRepository.findById(d.getNeighborhoodId()).orElse(null));
        if (d.getOwnerId()!=null) p.setOwner(userRepository.findById(d.getOwnerId()).orElse(null));
        if (d.getAgentId()!=null) p.setAgent(userRepository.findById(d.getAgentId()).orElse(null));
        p.setStatus(d.getStatus());
        p.setLocationLat(d.getLocationLat());
        p.setLocationLng(d.getLocationLng());
        p.setPublishedAt(d.getPublishedAt());
        p.setIsDeleted(d.getIsDeleted());
        // images, amenities, priceHistory not fully created here — use separate endpoints or extend mapping
        return p;
    }

    public Page<PropertyDto> list(Pageable pageable) {
        return propertyRepository.findAll(pageable).map(this::toDto);
    }

    public Page<PropertyDto> search(String listingType, String city, String category, String q, Pageable pageable) {
        // If no search parameters, return all
        if (listingType == null && city == null && category == null && q == null) {
            return list(pageable);
        }

        CriteriaBuilder cb = entityManager.getCriteriaBuilder();
        CriteriaQuery<Property> query = cb.createQuery(Property.class);
        Root<Property> root = query.from(Property.class);
        // Build search specification

        jakarta.persistence.criteria.Predicate predicate = cb.conjunction();

        // Search by listing type
        if (listingType != null && !listingType.trim().isEmpty()) {
            predicate = cb.and(predicate, cb.equal(root.get("listingType"),
                    com.amin.aggar.domain.enums.ListingType.fromValue(listingType)));
        }

        // Search by city name
        if (city != null && !city.trim().isEmpty()) {
            predicate = cb.and(predicate, cb.like(cb.lower(root.get("city").get("name")),
                    "%" + city.toLowerCase() + "%"));
        }

        // Search by category (could map to property type in description/title)
        if (category != null && !category.trim().isEmpty()) {
            String categoryLower = category.toLowerCase();
            predicate = cb.and(predicate, cb.or(
                    cb.like(cb.lower(root.get("title")), "%" + categoryLower + "%"),
                    cb.like(cb.lower(root.get("description")), "%" + categoryLower + "%")
            ));
        }

        // Search by query string (title, description, city, neighborhood)
        if (q != null && !q.trim().isEmpty()) {
            String queryLower = q.toLowerCase();
            predicate = cb.and(predicate, cb.or(
                    cb.like(cb.lower(root.get("title")), "%" + queryLower + "%"),
                    cb.like(cb.lower(root.get("description")), "%" + queryLower + "%"),
                    cb.like(cb.lower(root.get("city").get("name")), "%" + queryLower + "%"),
                    cb.like(cb.lower(root.get("neighborhood").get("name")), "%" + queryLower + "%")
            ));
        }

        // Only show non-deleted properties
        predicate = cb.and(predicate, cb.or(
                cb.isNull(root.get("isDeleted")),
                cb.equal(root.get("isDeleted"), false)
        ));

        //predicate;

        return entityManager.createQuery(query).getResultList().stream().map(this::toDto).toList();

        //}, pageable).map(this::toDto);
    }

    public Optional<PropertyDto> findById(Long id) { return propertyRepository.findById(id).map(this::toDto); }

    @Transactional
    public PropertyDto create(PropertyDto dto) {
        Property p = fromDto(dto);
        p.setId(null);
        Property saved = propertyRepository.save(p);
        return toDto(saved);
    }

    @Transactional
    public Optional<PropertyDto> update(Long id, PropertyDto dto) {
        return propertyRepository.findById(id).map(existing -> {
            existing.setTitle(dto.getTitle());
            existing.setDescription(dto.getDescription());
            existing.setPrice(dto.getPrice());
            existing.setCurrency(dto.getCurrency());
            if (dto.getListingType()!=null) existing.setListingType(ListingType.fromValue(dto.getListingType()));
            existing.setBedrooms(dto.getBedrooms());
            existing.setBathrooms(dto.getBathrooms());
            existing.setArea(dto.getArea());
            if (dto.getStateId()!=null) existing.setState(stateRepository.findById(dto.getStateId()).orElse(null));
            if (dto.getCityId()!=null) existing.setCity(cityRepository.findById(dto.getCityId()).orElse(null));
            if (dto.getNeighborhoodId()!=null) existing.setNeighborhood(neighborhoodRepository.findById(dto.getNeighborhoodId()).orElse(null));
            if (dto.getOwnerId()!=null) existing.setOwner(userRepository.findById(dto.getOwnerId()).orElse(null));
            if (dto.getAgentId()!=null) existing.setAgent(userRepository.findById(dto.getAgentId()).orElse(null));
            existing.setStatus(dto.getStatus());
            existing.setLocationLat(dto.getLocationLat());
            existing.setLocationLng(dto.getLocationLng());
            Property saved = propertyRepository.save(existing);
            return toDto(saved);
        });
    }

    @Transactional
    public boolean delete(Long id) { return propertyRepository.findById(id).map(p -> { propertyRepository.delete(p); return true; }).orElse(false); }
}
