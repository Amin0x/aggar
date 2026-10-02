package com.amin.aggar.service;

import com.amin.aggar.api.dto.*;
import com.amin.aggar.domain.entity.*;
import com.amin.aggar.domain.enums.ListingType;
import com.amin.aggar.repository.*;
import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Root;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class PropertyService {

    private static final Pattern SLUG_PATTERN = Pattern.compile("[^a-zA-Z0-9]+");

    private final PropertyRepository propertyRepository;
    private final StateRepository stateRepository;
    private final CityRepository cityRepository;
    private final NeighborhoodRepository neighborhoodRepository;
    private final UserRepository userRepository;
    private final AmenityRepository amenityRepository;
    private final PropertyImageRepository propertyImageRepository;
    private final PriceHistoryRepository priceHistoryRepository;
    private final EntityManager entityManager;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    private String generateSlug(String title) {
        if (title == null || title.trim().isEmpty()) {
            return "property-" + System.currentTimeMillis();
        }
        String slug = SLUG_PATTERN.matcher(title.toLowerCase().trim()).replaceAll("-");
        slug = slug.replaceAll("^-|-$", "");
        return slug.isEmpty() ? "property-" + System.currentTimeMillis() : slug;
    }

    private String uniqueSlug(String slug, Long propertyId) {
        String baseSlug = slug;
        String candidate = baseSlug;
        while (propertyRepository.findBySlug(candidate)
                .filter(existing -> !existing.getId().equals(propertyId))
                .isPresent()) {
            String suffix = Integer.toString(ThreadLocalRandom.current().nextInt(100000, 1000000));
            String shortenedBase = baseSlug.substring(0, Math.min(baseSlug.length(), 255 - suffix.length() - 1));
            candidate = shortenedBase + "-" + suffix;
        }
        return candidate;
    }

    private String buildFullImageUrl(String url) {
        if (url == null || url.isEmpty()) {
            return url;
        }
        // If URL is already absolute (starts with http:// or https://), return as-is
        if (url.startsWith("http://") || url.startsWith("https://")) {
            return url;
        }
        // Otherwise, prepend the base URL
        String base = baseUrl.endsWith("/") ? baseUrl : baseUrl + "/";
        String imagePath = url.startsWith("/") ? url.substring(1) : url;
        return base + imagePath;
    }

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
        d.setSlug(p.getSlug());
        d.setDescription(p.getDescription());
        d.setPrice(p.getPrice());
        d.setViewCount(p.getViewCount());
        d.setCurrency(p.getCurrency());
        d.setListingType(p.getListingType() != null ? p.getListingType().getValue() : null);
        d.setCategory(p.getCategory());
        d.setPricePeriod(p.getPricePeriod() != null ? p.getPricePeriod().getValue() : null);
        d.setBedrooms(p.getBedrooms());
        d.setBathrooms(p.getBathrooms());
        d.setArea(p.getArea());
        d.setStateId(p.getState() != null ? p.getState().getId() : null);
        d.setCityId(p.getCity() != null ? p.getCity().getId() : null);
        d.setNeighborhoodId(p.getNeighborhood() != null ? p.getNeighborhood().getId() : null);
        d.setOwnerId(p.getOwner() != null ? p.getOwner().getId() : null);
        d.setAgentId(p.getAgent() != null ? p.getAgent().getId() : null);
        // Set owner contact info
        if (p.getOwner() != null) {
            d.setOwnerName(p.getOwner().getName());
            d.setOwnerPhone(p.getOwner().getPhone());
        }
        // Set agent contact info
        if (p.getAgent() != null) {
            d.setAgentName(p.getAgent().getName());
            d.setAgentPhone(p.getAgent().getPhone());
        }
        d.setStatus(p.getStatus());
        d.setLocationLat(p.getLocationLat());
        d.setLocationLng(p.getLocationLng());
        d.setPublishedAt(p.getPublishedAt());
        d.setCreatedAt(p.getCreatedAt());
        d.setUpdatedAt(p.getUpdatedAt());
        d.setIsDeleted(p.getIsDeleted());
        // images
        if (p.getImages() != null) {
            d.setImages(p.getImages().stream().map(img -> {
                PropertyImageDto idto = new PropertyImageDto();
                idto.setId(img.getId());
                idto.setPropertyId(p.getId());
                idto.setUrl(buildFullImageUrl(img.getUrl()));
                idto.setIsPrimary(img.getIsPrimary());
                idto.setSortOrder(img.getSortOrder());
                return idto;
            }).collect(Collectors.toList()));
        }
        // amenities
        if (p.getAmenities() != null) {
            d.setAmenities(p.getAmenities().stream().map(a -> {
                AmenityDto ad = new AmenityDto();
                ad.setId(a.getId());
                ad.setName(a.getName());
                return ad;
            }).collect(Collectors.toSet()));
        }
        // price history omitted (can add similarly)
        return d;
    }

    private Property fromDto(PropertyDto d) {
        if (d == null) return null;
        Property p = new Property();
        p.setId(d.getId());
        p.setTitle(d.getTitle());
        p.setSlug(d.getSlug());
        p.setDescription(d.getDescription());
        p.setPrice(d.getPrice());
        p.setCurrency(d.getCurrency());
        if (d.getListingType() != null) p.setListingType(ListingType.fromValue(d.getListingType()));
        p.setCategory(d.getCategory());
        // pricePeriod mapping omitted for brevity
        p.setBedrooms(d.getBedrooms());
        p.setBathrooms(d.getBathrooms());
        p.setArea(d.getArea());
        if (d.getStateId() != null) p.setState(stateRepository.findById(d.getStateId()).orElse(null));
        if (d.getCityId() != null) p.setCity(cityRepository.findById(d.getCityId()).orElse(null));
        if (d.getNeighborhoodId() != null)
            p.setNeighborhood(neighborhoodRepository.findById(d.getNeighborhoodId()).orElse(null));
        if (d.getOwnerId() != null) p.setOwner(userRepository.findById(d.getOwnerId()).orElse(null));
        if (d.getAgentId() != null) p.setAgent(userRepository.findById(d.getAgentId()).orElse(null));
        p.setStatus(d.getStatus());
        p.setLocationLat(d.getLocationLat());
        p.setLocationLng(d.getLocationLng());
        p.setPublishedAt(d.getPublishedAt());
        p.setIsDeleted(d.getIsDeleted());
        // images, amenities, priceHistory not fully created here — use separate endpoints or extend mapping
        return p;
    }

    public Page<PropertyDto> list(Pageable pageable) {
        Page<Property> page = propertyRepository.findAll(pageable);
        return page.map(this::toDto);
    }

    public Page<PropertyDto> search(String listingType, String city, String category, String q, Pageable pageable) {
        CriteriaBuilder cb = entityManager.getCriteriaBuilder();

        // 1. Create the Data Query
        CriteriaQuery<Property> query = cb.createQuery(Property.class);
        Root<Property> root = query.from(Property.class);

        // Fetch *ToOne associations to avoid N+1 selects
        root.fetch("state", JoinType.LEFT);
        root.fetch("city", JoinType.LEFT);
        root.fetch("neighborhood", JoinType.LEFT);
        root.fetch("owner", JoinType.LEFT);
        root.fetch("agent", JoinType.LEFT);

        // 2. Build the Predicate (Extracted for reuse in the count query)
        jakarta.persistence.criteria.Predicate predicate = buildPredicate(cb, root, listingType, city, category, q);

        // 3. Apply Predicate and Sort
        query.where(predicate);
        if (pageable.getSort().isSorted()) {
            query.orderBy(QueryUtils.toOrders(pageable.getSort(), root, cb));
        }

        // 4. Fetch the Data for the current page
        List<Property> resultList = entityManager.createQuery(query)
                .setFirstResult((int) pageable.getOffset())
                .setMaxResults(pageable.getPageSize())
                .getResultList();

        // 5. Create the Count Query (Required for Pagination)
        CriteriaQuery<Long> countQuery = cb.createQuery(Long.class);
        Root<Property> countRoot = countQuery.from(Property.class);
        countQuery.select(cb.count(countRoot));
        countQuery.where(buildPredicate(cb, countRoot, listingType, city, category, q));

        Long totalCount = entityManager.createQuery(countQuery).getSingleResult();

        // 6. Map to DTO and Return Page
        List<PropertyDto> dtos = resultList.stream().map(this::toDto).toList();
        return new PageImpl<>(dtos, pageable, totalCount);
    }

    // Helper to keep logic DRY (Don't Repeat Yourself)
    private jakarta.persistence.criteria.Predicate buildPredicate(CriteriaBuilder cb, Root<Property> root,
                                                                  String listingType, String city,
                                                                  String category, String q) {
        jakarta.persistence.criteria.Predicate predicate = cb.conjunction();

        if (listingType != null && !listingType.trim().isEmpty()) {
            predicate = cb.and(predicate, cb.equal(root.get("listingType"),
                    com.amin.aggar.domain.enums.ListingType.fromValue(listingType)));
        }

        if (city != null && !city.trim().isEmpty()) {
            predicate = cb.and(predicate, cb.like(cb.lower(root.get("city").get("name")),
                    "%" + city.toLowerCase() + "%"));
        }

        if (category != null && !category.trim().isEmpty()) {
            String catLow = category.toLowerCase();
            predicate = cb.and(predicate, cb.or(
                    cb.like(cb.lower(root.get("title")), "%" + catLow + "%"),
                    cb.like(cb.lower(root.get("description")), "%" + catLow + "%"),
                    cb.like(cb.lower(root.get("category")), "%" + catLow + "%")
            ));
        }

        if (q != null && !q.trim().isEmpty()) {
            String qLow = q.toLowerCase();
            predicate = cb.and(predicate, cb.or(
                    cb.like(cb.lower(root.get("title")), "%" + qLow + "%"),
                    cb.like(cb.lower(root.get("description")), "%" + qLow + "%"),
                    cb.like(cb.lower(root.get("city").get("name")), "%" + qLow + "%"),
                    cb.like(cb.lower(root.get("neighborhood").get("name")), "%" + qLow + "%")
            ));
        }

        // Soft delete check
        predicate = cb.and(predicate, cb.or(
                cb.isNull(root.get("isDeleted")),
                cb.equal(root.get("isDeleted"), false)
        ));

        // Exclude pending and rejected properties from public search
        jakarta.persistence.criteria.Expression<String> statusExpr = root.get("status");
        predicate = cb.and(predicate,
                cb.or(
                        cb.not(cb.equal(statusExpr, "pending")),
                        cb.isNull(statusExpr)
                ),
                cb.or(
                        cb.not(cb.equal(statusExpr, "rejected")),
                        cb.isNull(statusExpr)
                )
        );

        return predicate;
    }

    public Optional<PropertyDto> findById(Long id) {
        return propertyRepository.findById(id).map(this::toDto);
    }

    public Optional<PropertyDto> findBySlug(String slug) {
        return propertyRepository.findBySlug(slug).map(this::toDto);
    }

    @Transactional
    public Optional<PropertyDto> recordViewById(Long id) {
        if (propertyRepository.incrementViewCountById(id) == 0) {
            return Optional.empty();
        }
        return propertyRepository.findById(id).map(this::toDto);
    }

    @Transactional
    public Optional<PropertyDto> recordViewBySlug(String slug) {
        if (propertyRepository.incrementViewCountBySlug(slug) == 0) {
            return Optional.empty();
        }
        return propertyRepository.findBySlug(slug).map(this::toDto);
    }

    @Transactional
    public PropertyDto create(PropertyDto dto) {
        Property p = fromDto(dto);
        p.setId(null);
        if (p.getSlug() == null || p.getSlug().trim().isEmpty()) {
            p.setSlug(generateSlug(p.getTitle()));
        }
        p.setSlug(uniqueSlug(p.getSlug(), null));
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
            if (dto.getListingType() != null) existing.setListingType(ListingType.fromValue(dto.getListingType()));
            existing.setCategory(dto.getCategory());
            existing.setBedrooms(dto.getBedrooms());
            existing.setBathrooms(dto.getBathrooms());
            existing.setArea(dto.getArea());
            if (dto.getStateId() != null) existing.setState(stateRepository.findById(dto.getStateId()).orElse(null));
            if (dto.getCityId() != null) existing.setCity(cityRepository.findById(dto.getCityId()).orElse(null));
            if (dto.getNeighborhoodId() != null)
                existing.setNeighborhood(neighborhoodRepository.findById(dto.getNeighborhoodId()).orElse(null));
            if (dto.getOwnerId() != null) existing.setOwner(userRepository.findById(dto.getOwnerId()).orElse(null));
            if (dto.getAgentId() != null) existing.setAgent(userRepository.findById(dto.getAgentId()).orElse(null));
            existing.setStatus(dto.getStatus());
            existing.setLocationLat(dto.getLocationLat());
            existing.setLocationLng(dto.getLocationLng());
            if (dto.getSlug() != null && !dto.getSlug().trim().isEmpty()) {
                existing.setSlug(uniqueSlug(dto.getSlug(), existing.getId()));
            }
            Property saved = propertyRepository.save(existing);
            return toDto(saved);
        });
    }

    @Transactional
    public boolean delete(Long id) {
        return propertyRepository.findById(id).map(p -> {
            propertyRepository.delete(p);
            return true;
        }).orElse(false);
    }

    public Page<PropertyDto> findByStatus(String status, Pageable pageable) {
        Page<Property> page = propertyRepository.findByStatus(status, pageable);
        return page.map(this::toDto);
    }

    @Transactional
    public Optional<PropertyDto> approve(Long id) {
        return propertyRepository.findById(id).map(p -> {
            p.setStatus("available");
            p.setPublishedAt(java.time.LocalDateTime.now());
            Property saved = propertyRepository.save(p);
            return toDto(saved);
        });
    }

    @Transactional
    public Optional<PropertyDto> reject(Long id) {
        return propertyRepository.findById(id).map(p -> {
            p.setStatus("rejected");
            Property saved = propertyRepository.save(p);
            return toDto(saved);
        });
    }
}
