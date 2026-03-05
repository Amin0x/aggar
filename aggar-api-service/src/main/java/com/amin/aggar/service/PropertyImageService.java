package com.amin.aggar.service;

import com.amin.aggar.api.dto.PropertyImageDto;
import com.amin.aggar.domain.entity.Property;
import com.amin.aggar.domain.entity.PropertyImage;
import com.amin.aggar.repository.PropertyImageRepository;
import com.amin.aggar.repository.PropertyRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.Optional;

@Service
public class PropertyImageService {

    private final PropertyImageRepository propertyImageRepository;
    private final PropertyRepository propertyRepository;

    public PropertyImageService(PropertyImageRepository propertyImageRepository, PropertyRepository propertyRepository) {
        this.propertyImageRepository = propertyImageRepository;
        this.propertyRepository = propertyRepository;
    }

    private PropertyImageDto toDto(PropertyImage p) {
        if (p == null) return null;
        PropertyImageDto d = new PropertyImageDto();
        d.setId(p.getId());
        d.setPropertyId(p.getProperty() != null ? p.getProperty().getId() : null);
        d.setUrl(p.getUrl());
        d.setIsPrimary(p.getIsPrimary());
        d.setSortOrder(p.getSortOrder());
        return d;
    }

    private PropertyImage fromDto(PropertyImageDto d) {
        if (d == null) return null;
        PropertyImage p = new PropertyImage();
        p.setId(d.getId());
        if (d.getPropertyId() != null) p.setProperty(propertyRepository.findById(d.getPropertyId()).orElse(null));
        p.setUrl(d.getUrl());
        p.setIsPrimary(d.getIsPrimary());
        p.setSortOrder(d.getSortOrder());
        return p;
    }

    public Page<PropertyImageDto> list(Pageable pageable) {
        return propertyImageRepository.findAll(pageable).map(this::toDto);
    }

    public Optional<PropertyImageDto> findById(Long id) {
        return propertyImageRepository.findById(id).map(this::toDto);
    }

    @Transactional
    public PropertyImageDto create(PropertyImageDto dto) {
        PropertyImage p = fromDto(dto);
        p.setId(null);
        PropertyImage saved = propertyImageRepository.save(p);
        return toDto(saved);
    }

    @Transactional
    public Optional<PropertyImageDto> update(Long id, PropertyImageDto dto) {
        return propertyImageRepository.findById(id).map(existing -> {
            if (dto.getPropertyId() != null) existing.setProperty(propertyRepository.findById(dto.getPropertyId()).orElse(null));
            existing.setUrl(dto.getUrl());
            existing.setIsPrimary(dto.getIsPrimary());
            existing.setSortOrder(dto.getSortOrder());
            return toDto(propertyImageRepository.save(existing));
        });
    }

    @Transactional
    public boolean delete(Long id) {
        return propertyImageRepository.findById(id).map(p -> {
            propertyImageRepository.delete(p);
            return true;
        }).orElse(false);
    }
}

