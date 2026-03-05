package com.amin.aggar.service;

import com.amin.aggar.api.dto.NeighborhoodDto;
import com.amin.aggar.domain.entity.City;
import com.amin.aggar.domain.entity.Neighborhood;
import com.amin.aggar.repository.CityRepository;
import com.amin.aggar.repository.NeighborhoodRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.Optional;

@Service
public class NeighborhoodService {

    private final NeighborhoodRepository neighborhoodRepository;
    private final CityRepository cityRepository;

    public NeighborhoodService(NeighborhoodRepository neighborhoodRepository, CityRepository cityRepository) {
        this.neighborhoodRepository = neighborhoodRepository;
        this.cityRepository = cityRepository;
    }

    private NeighborhoodDto toDto(Neighborhood n) {
        if (n == null) return null;
        NeighborhoodDto d = new NeighborhoodDto();
        d.setId(n.getId());
        d.setCityId(n.getCity() != null ? n.getCity().getId() : null);
        d.setName(n.getName());
        return d;
    }

    private Neighborhood fromDto(NeighborhoodDto d) {
        if (d == null) return null;
        Neighborhood n = new Neighborhood();
        n.setId(d.getId());
        if (d.getCityId() != null) {
            City c = cityRepository.findById(d.getCityId()).orElse(null);
            n.setCity(c);
        }
        n.setName(d.getName());
        return n;
    }

    public Page<NeighborhoodDto> list(Pageable pageable) {
        return neighborhoodRepository.findAll(pageable).map(this::toDto);
    }

    public Optional<NeighborhoodDto> findById(Integer id) {
        return neighborhoodRepository.findById(id).map(this::toDto);
    }

    @Transactional
    public NeighborhoodDto create(NeighborhoodDto dto) {
        Neighborhood n = fromDto(dto);
        n.setId(null);
        Neighborhood saved = neighborhoodRepository.save(n);
        return toDto(saved);
    }

    @Transactional
    public Optional<NeighborhoodDto> update(Integer id, NeighborhoodDto dto) {
        return neighborhoodRepository.findById(id).map(existing -> {
            existing.setName(dto.getName());
            if (dto.getCityId() != null) {
                City c = cityRepository.findById(dto.getCityId()).orElse(null);
                existing.setCity(c);
            }
            return toDto(neighborhoodRepository.save(existing));
        });
    }

    @Transactional
    public boolean delete(Integer id) {
        return neighborhoodRepository.findById(id).map(n -> {
            neighborhoodRepository.delete(n);
            return true;
        }).orElse(false);
    }
}

