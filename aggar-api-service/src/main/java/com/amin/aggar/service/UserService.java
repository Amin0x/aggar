package com.amin.aggar.service;

import com.amin.aggar.api.dto.UserDto;
import com.amin.aggar.domain.entity.User;
import com.amin.aggar.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    private UserDto toDto(User u) {
        if (u == null) return null;
        UserDto d = new UserDto();
        d.setId(u.getId());
        d.setName(u.getName());
        d.setEmail(u.getEmail());
        d.setPhone(u.getPhone());
        d.setRole(u.getRole());
        d.setCreatedAt(u.getCreatedAt());
        return d;
    }

    private User fromDto(UserDto d) {
        if (d == null) return null;
        User u = new User();
        u.setId(d.getId());
        u.setName(d.getName());
        u.setEmail(d.getEmail());
        u.setPhone(d.getPhone());
        u.setRole(d.getRole());
        u.setCreatedAt(d.getCreatedAt() == null ? LocalDateTime.now() : d.getCreatedAt());
        return u;
    }

    public List<UserDto> listAll() {
        return userRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public Optional<UserDto> findById(Integer id) {
        return userRepository.findById(id).map(this::toDto);
    }

    @Transactional
    public UserDto create(UserDto dto) {
        User u = fromDto(dto);
        u.setId(null);
        User saved = userRepository.save(u);
        return toDto(saved);
    }

    @Transactional
    public Optional<UserDto> update(Integer id, UserDto dto) {
        return userRepository.findById(id).map(existing -> {
            existing.setName(dto.getName());
            existing.setEmail(dto.getEmail());
            existing.setPhone(dto.getPhone());
            existing.setRole(dto.getRole());
            User saved = userRepository.save(existing);
            return toDto(saved);
        });
    }

    @Transactional
    public boolean delete(Integer id) {
        return userRepository.findById(id).map(u -> {
            userRepository.delete(u);
            return true;
        }).orElse(false);
    }
}

