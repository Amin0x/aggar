package com.amin.aggar.service;

import com.amin.aggar.api.dto.CityDto;
import com.amin.aggar.domain.entity.City;
import com.amin.aggar.domain.entity.State;
import com.amin.aggar.repository.CityRepository;
import com.amin.aggar.repository.StateRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.Optional;

@Service
public class CityService {

    private final CityRepository cityRepository;
    private final StateRepository stateRepository;

    public CityService(CityRepository cityRepository, StateRepository stateRepository) {
        this.cityRepository = cityRepository;
        this.stateRepository = stateRepository;
    }

    private CityDto toDto(City c) {
        if (c == null) return null;
        CityDto d = new CityDto();
        d.setId(c.getId());
        d.setStateId(c.getState() != null ? c.getState().getId() : null);
        d.setName(c.getName());
        return d;
    }

    private City fromDto(CityDto d) {
        if (d == null) return null;
        City c = new City();
        c.setId(d.getId());
        if (d.getStateId() != null) {
            State s = stateRepository.findById(d.getStateId()).orElse(null);
            c.setState(s);
        }
        c.setName(d.getName());
        return c;
    }

    public Page<CityDto> list(Pageable pageable) {
        return cityRepository.findAll(pageable).map(this::toDto);
    }

    public Optional<CityDto> findById(Integer id) {
        return cityRepository.findById(id).map(this::toDto);
    }

    @Transactional
    public CityDto create(CityDto dto) {
        City c = fromDto(dto);
        c.setId(null);
        City saved = cityRepository.save(c);
        return toDto(saved);
    }

    @Transactional
    public Optional<CityDto> update(Integer id, CityDto dto) {
        return cityRepository.findById(id).map(existing -> {
            existing.setName(dto.getName());
            if (dto.getStateId() != null) {
                State s = stateRepository.findById(dto.getStateId()).orElse(null);
                existing.setState(s);
            }
            return toDto(cityRepository.save(existing));
        });
    }

    @Transactional
    public boolean delete(Integer id) {
        return cityRepository.findById(id).map(c -> {
            cityRepository.delete(c);
            return true;
        }).orElse(false);
    }
}

