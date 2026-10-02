package com.amin.aggar.config;

import com.amin.aggar.repository.PropertyRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

@Component("propertyAuthorization")
public class PropertyAuthorization {

    private final PropertyRepository propertyRepository;

    public PropertyAuthorization(PropertyRepository propertyRepository) {
        this.propertyRepository = propertyRepository;
    }

    public boolean canManage(Long propertyId, String username) {
        if (propertyId == null || username == null) {
            return false;
        }
        return propertyRepository.findById(propertyId)
                .map(property ->
                        property.getOwner() != null && username.equals(property.getOwner().getUsername())
                                || property.getAgent() != null && username.equals(property.getAgent().getUsername()))
                .orElse(false);
    }

    public boolean canView(Long propertyId, Authentication authentication) {
        if (propertyId == null) {
            return false;
        }
        return propertyRepository.findById(propertyId)
                .map(property -> canViewProperty(property, authentication))
                .orElse(false);
    }

    public boolean canViewBySlug(String slug, Authentication authentication) {
        if (slug == null || slug.isBlank()) {
            return false;
        }
        return propertyRepository.findBySlug(slug)
                .map(property -> canViewProperty(property, authentication))
                .orElse(false);
    }

    private boolean canViewProperty(
            com.amin.aggar.domain.entity.Property property, Authentication authentication) {
        if (Boolean.TRUE.equals(property.getIsDeleted())) {
            return false;
        }
        if (!"pending".equalsIgnoreCase(property.getStatus())
                && !"rejected".equalsIgnoreCase(property.getStatus())) {
            return true;
        }
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getName())) {
            return false;
        }
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"))
                || canManage(property.getId(), authentication.getName());
    }
}
