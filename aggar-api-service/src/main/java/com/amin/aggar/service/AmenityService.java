package com.amin.aggar.service;

import com.amin.aggar.api.dto.AmenityDto;
import com.amin.aggar.domain.entity.Amenity;
import com.amin.aggar.repository.AmenityRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.Optional;

@Service
public class AmenityService {

    private final AmenityRepository amenityRepository;

    public AmenityService(AmenityRepository amenityRepository) {
        this.amenityRepository = amenityRepository;
    }

    private AmenityDto toDto(Amenity a) {
        if (a == null) return null;
        AmenityDto d = new AmenityDto();
        d.setId(a.getId());
        d.setName(a.getName());
        return d;
    }

    private Amenity fromDto(AmenityDto d) {
        if (d == null) return null;
        Amenity a = new Amenity();
        a.setId(d.getId());
        a.setName(d.getName());
        return a;
    }

    public Page<AmenityDto> list(Pageable pageable) {
        return amenityRepository.findAll(pageable).map(this::toDto);
    }

    public Optional<AmenityDto> findById(Integer id) {
        return amenityRepository.findById(id).map(this::toDto);
    }

    @Transactional
    public AmenityDto create(AmenityDto dto) {
        Amenity a = fromDto(dto);
        a.setId(null);
        Amenity saved = amenityRepository.save(a);
        return toDto(saved);
    }

    @Transactional
    public Optional<AmenityDto> update(Integer id, AmenityDto dto) {
        return amenityRepository.findById(id).map(existing -> {
            existing.setName(dto.getName());
            return toDto(amenityRepository.save(existing));
        });
    }

    @Transactional
    public boolean delete(Integer id) {
        return amenityRepository.findById(id).map(a -> {
            amenityRepository.delete(a);
            return true;
        }).orElse(false);
    }
}

