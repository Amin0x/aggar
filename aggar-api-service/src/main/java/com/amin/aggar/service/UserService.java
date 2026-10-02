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
        UserDto d = new UserDto();
        d.setId(u.getId());
        d.setUsername(u.getUsername());
        d.setPassword(u.getPassword());
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
        u.setUsername(d.getUsername());
        u.setPassword(d.getPassword());
        u.setName(d.getName());
        u.setEmail(d.getEmail());
        u.setPhone(d.getPhone());
        u.setRole(d.getRole());
        u.setCreatedAt(d.getCreatedAt() == null ? LocalDateTime.now() : d.getCreatedAt());
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
        if (dto == null || dto.getUsername() == null || dto.getUsername().isBlank()
                || dto.getPassword() == null || dto.getPassword().length() < 8
                || dto.getName() == null || dto.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Username, name, and an 8-character password are required");
        }
        if (userRepository.findByUsername(dto.getUsername().trim()).isPresent()
                || dto.getEmail() != null && userRepository.findByEmailIgnoreCase(dto.getEmail().trim()).isPresent()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Username or email is already registered");
        }
        dto.setUsername(dto.getUsername().trim());
        dto.setRole("owner");
        User u = fromDto(dto);
        u.setId(null);
        u.setPassword(passwordEncoder.encode(dto.getPassword()));
        User saved = userRepository.save(u);
        return toDto(saved);
    }

    @Transactional
    public Optional<UserDto> update(Long id, UserDto dto, boolean admin) {
        return userRepository.findById(id).map(existing -> {
            existing.setName(dto.getName());
            existing.setEmail(dto.getEmail());
            existing.setPhone(dto.getPhone());
            if (admin && dto.getRole() != null) {
                existing.setRole(dto.getRole());
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
