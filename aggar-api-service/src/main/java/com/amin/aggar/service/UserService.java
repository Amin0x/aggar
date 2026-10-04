package com.amin.aggar.service;

import com.amin.aggar.api.dto.UserDto;
import com.amin.aggar.api.dto.AuthenticatedUserDto;
import com.amin.aggar.domain.entity.User;
import com.amin.aggar.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public UserService(UserRepository userRepository) {
        this(userRepository, new BCryptPasswordEncoder());
    }

    private UserDto toDto(User u) {
        if (u == null) return null;
        UserDto d = new UserDto(
                u.getId(),
                u.getUsername(),
                u.getPassword(),
                u.getName(),
                u.getEmail(),
                u.getPhone(),
                u.getRole(),
                u.getCreatedAt()
        );
        return d;
    }

    private User fromDto(UserDto d) {
        if (d == null) return null;
        User u = new User();
        u.setId(d.id());
        u.setUsername(d.username());
        u.setPassword(d.password());
        u.setName(d.name());
        u.setEmail(d.email());
        u.setPhone(d.phone());
        u.setRole(d.role());
        u.setCreatedAt(d.createdAt() == null ? LocalDateTime.now() : d.createdAt());
        return u;
    }

    public Optional<UserDto> findByUsername(String username) {
        return userRepository.findByUsername(username).map(this::toDto);
    }

    public boolean validatePassword(String rawPassword, String encodedPassword) {
        if (rawPassword == null || encodedPassword == null) {
            return false;
        }
        if (encodedPassword.startsWith("$2a$") || encodedPassword.startsWith("$2b$")
                || encodedPassword.startsWith("$2y$")) {
            return passwordEncoder.matches(rawPassword, encodedPassword);
        }
        return rawPassword.equals(encodedPassword);
    }

    @Transactional
    public Optional<AuthenticatedUserDto> authenticate(String identifier, String rawPassword) {
        if (identifier == null || identifier.isBlank() || rawPassword == null) {
            return Optional.empty();
        }

        String normalizedIdentifier = identifier.trim();
        Optional<User> user = userRepository.findByUsername(normalizedIdentifier)
                .or(() -> userRepository.findByEmailIgnoreCase(normalizedIdentifier));

        return user.filter(candidate -> validatePassword(rawPassword, candidate.getPassword()))
                .map(candidate -> {
                    if (!candidate.getPassword().startsWith("$2")) {
                        candidate.setPassword(passwordEncoder.encode(rawPassword));
                        userRepository.save(candidate);
                    }
                    return new AuthenticatedUserDto(
                        candidate.getId(),
                        candidate.getUsername(),
                        candidate.getName(),
                        candidate.getEmail(),
                        candidate.getPhone(),
                        candidate.getRole(),
                        candidate.getCreatedAt()
                    );
                });
    }

    public List<UserDto> listAll() {
        return userRepository.findAll().stream().map(this::toDto).collect(Collectors.toList());
    }

    public Optional<UserDto> findById(Long id) {
        return userRepository.findById(id).map(this::toDto);
    }

    @Transactional
    public UserDto create(UserDto dto) {
        if (dto == null || dto.username() == null || dto.username().isBlank()
                || dto.password() == null || dto.password().length() < 8
                || dto.name() == null || dto.name().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username, name, and an 8-character password are required");
        }
        if (userRepository.findByUsername(dto.username().trim()).isPresent()
                || dto.email() != null && userRepository.findByEmailIgnoreCase(dto.email().trim()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username or email is already registered");
        }
        dto = new UserDto(
                null,
                dto.username(),
                passwordEncoder.encode(dto.password()),
                dto.username().trim(),
                dto.email(),
                dto.phone(),
                "owner",
                dto.createdAt()
        );

        User u = fromDto(dto);

        User saved = userRepository.save(u);
        return toDto(saved);
    }

    @Transactional
    public Optional<UserDto> update(Long id, UserDto dto, boolean admin) {
        return userRepository.findById(id).map(existing -> {
            existing.setName(dto.name());
            existing.setEmail(dto.email());
            existing.setPhone(dto.phone());
            if (admin && dto.role() != null) {
                existing.setRole(dto.role());
            }
            User saved = userRepository.save(existing);
            return toDto(saved);
        });
    }

    @Transactional
    public boolean delete(Long id) {
        return userRepository.findById(id).map(u -> {
            userRepository.delete(u);
            return true;
        }).orElse(false);
    }
}
