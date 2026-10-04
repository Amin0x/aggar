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
import org.springframework.http.HttpStatus;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
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
        List<PropertyImageDto> images = p.getImages() == null ? null : p.getImages().stream()
                .map(img -> new PropertyImageDto(
                        img.getId(),
                        p.getId(),
                        buildFullImageUrl(img.getUrl()),
                        img.getIsPrimary(),
                        img.getSortOrder()
                ))
                .collect(Collectors.toList());
        Set<AmenityDto> amenities = p.getAmenities() == null ? null : p.getAmenities().stream()
                .map(a -> new AmenityDto(a.getId(), a.getName()))
                .collect(Collectors.toSet());
        return new PropertyDto(
                p.getId(),
                p.getTitle(),
                p.getSlug(),
                p.getDescription(),
                p.getPrice(),
                p.getViewCount(),
                p.getCurrency(),
                p.getListingType() != null ? p.getListingType().getValue() : null,
                p.getCategory(),
                p.getPricePeriod() != null ? p.getPricePeriod().getValue() : null,
                p.getBedrooms(),
                p.getBathrooms(),
                p.getArea(),
                p.getState() != null ? p.getState().getId() : null,
                p.getCity() != null ? p.getCity().getId() : null,
                p.getNeighborhood() != null ? p.getNeighborhood().getId() : null,
                p.getOwner() != null ? p.getOwner().getId() : null,
                p.getAgent() != null ? p.getAgent().getId() : null,
                p.getStatus(),
                p.getLocationLat(),
                p.getLocationLng(),
                p.getPublishedAt(),
                p.getCreatedAt(),
                p.getUpdatedAt(),
                p.getIsDeleted(),
                images,
                amenities,
                null,
                p.getOwner() != null ? p.getOwner().getName() : null,
                p.getOwner() != null ? p.getOwner().getPhone() : null,
                p.getAgent() != null ? p.getAgent().getName() : null,
                p.getAgent() != null ? p.getAgent().getPhone() : null
        );
    }

    private Property fromDto(PropertyDto d) {
        if (d == null) return null;
        Property p = new Property();
        p.setId(d.id());
        p.setTitle(d.title());
        p.setSlug(d.slug());
        p.setDescription(d.description());
        p.setPrice(d.price());
        p.setCurrency(d.currency());
        if (d.listingType() != null) p.setListingType(ListingType.fromValue(d.listingType()));
        p.setCategory(d.category());
        // pricePeriod mapping omitted for brevity
        p.setBedrooms(d.bedrooms());
        p.setBathrooms(d.bathrooms());
        p.setArea(d.area());
        if (d.stateId() != null) p.setState(stateRepository.findById(d.stateId()).orElse(null));
        if (d.cityId() != null) p.setCity(cityRepository.findById(d.cityId()).orElse(null));
        if (d.neighborhoodId() != null)
            p.setNeighborhood(neighborhoodRepository.findById(d.neighborhoodId()).orElse(null));
        if (d.ownerId() != null) p.setOwner(userRepository.findById(d.ownerId()).orElse(null));
        if (d.agentId() != null) p.setAgent(userRepository.findById(d.agentId()).orElse(null));
        p.setStatus(d.status());
        p.setLocationLat(d.locationLat());
        p.setLocationLng(d.locationLng());
        p.setPublishedAt(d.publishedAt());
        p.setIsDeleted(d.isDeleted());
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
    public PropertyDto create(PropertyDto dto, String username, boolean admin) {
        Property p = fromDto(dto);
        p.setId(null);
        if (!admin) {
            p.setOwner(userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Authenticated user not found")));
            p.setAgent(null);
            p.setStatus("pending");
        }
        if (p.getSlug() == null || p.getSlug().trim().isEmpty()) {
            p.setSlug(generateSlug(p.getTitle()));
        }
        p.setSlug(uniqueSlug(p.getSlug(), null));
        Property saved = propertyRepository.save(p);
        return toDto(saved);
    }

    @Transactional
    public Optional<PropertyDto> update(Long id, PropertyDto dto, boolean admin) {
        return propertyRepository.findById(id).map(existing -> {
            existing.setTitle(dto.title());
            existing.setDescription(dto.description());
            existing.setPrice(dto.price());
            existing.setCurrency(dto.currency());
            if (dto.listingType() != null) existing.setListingType(ListingType.fromValue(dto.listingType()));
            existing.setCategory(dto.category());
            existing.setBedrooms(dto.bedrooms());
            existing.setBathrooms(dto.bathrooms());
            existing.setArea(dto.area());
            if (dto.stateId() != null) existing.setState(stateRepository.findById(dto.stateId()).orElse(null));
            if (dto.cityId() != null) existing.setCity(cityRepository.findById(dto.cityId()).orElse(null));
            if (dto.neighborhoodId() != null)
                existing.setNeighborhood(neighborhoodRepository.findById(dto.neighborhoodId()).orElse(null));
            if (admin) {
                existing.setOwner(dto.ownerId() == null ? null : userRepository.findById(dto.ownerId()).orElse(null));
                existing.setAgent(dto.agentId() == null ? null : userRepository.findById(dto.agentId()).orElse(null));
                existing.setStatus(dto.status());
            }
            existing.setLocationLat(dto.locationLat());
            existing.setLocationLng(dto.locationLng());
            if (dto.slug() != null && !dto.slug().trim().isEmpty()) {
                existing.setSlug(uniqueSlug(dto.slug(), existing.getId()));
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
