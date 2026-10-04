package com.amin.aggar.service;

import com.amin.aggar.api.dto.StateDto;
import com.amin.aggar.domain.entity.State;
import com.amin.aggar.repository.StateRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class StateService {

    private final StateRepository stateRepository;

    public StateService(StateRepository stateRepository) {
        this.stateRepository = stateRepository;
    }

    private StateDto toDto(State s) {
        if (s == null) return null;
        StateDto d = new StateDto(
                s.getId(),
                s.getName(),
                s.getNameAr(),
                s.getCode()
        );
        return d;
    }

    private State fromDto(StateDto d) {
        if (d == null) return null;
        State s = new State();
        s.setId(d.id());
        s.setName(d.name());
        s.setNameAr(d.nameAr());
        s.setCode(d.code());
        return s;
    }

    public List<StateDto> list() {
        return stateRepository.findAll().stream().map(this::toDto).toList();
    }

    public Optional<StateDto> findById(Integer id) {
        return stateRepository.findById(id).map(this::toDto);
    }

    @Transactional
    public StateDto create(StateDto dto) {
        State s = fromDto(dto);
        s.setId(null);
        State saved = stateRepository.save(s);
        return toDto(saved);
    }

    @Transactional
    public Optional<StateDto> update(Integer id, StateDto dto) {
        return stateRepository.findById(id).map(existing -> {
            existing.setName(dto.name());
            if (dto.nameAr() != null) {
                existing.setNameAr(dto.nameAr());
            }
            existing.setCode(dto.code());
            return toDto(stateRepository.save(existing));
        });
    }

    @Transactional
    public boolean delete(Integer id) {
        return stateRepository.findById(id).map(s -> {
            stateRepository.delete(s);
            return true;
        }).orElse(false);
    }
}
