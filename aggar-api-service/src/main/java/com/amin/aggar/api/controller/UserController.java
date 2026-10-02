package com.amin.aggar.api.controller;

import com.amin.aggar.api.dto.UserDto;
import com.amin.aggar.api.dto.AuthenticationRequest;
import com.amin.aggar.api.dto.JwtAuthenticationResponse;
import com.amin.aggar.config.UserAuthorization;
import com.amin.aggar.service.JwtTokenService;
import com.amin.aggar.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final JwtTokenService jwtTokenService;

    public UserController(UserService userService, JwtTokenService jwtTokenService) {
        this.userService = userService;
        this.jwtTokenService = jwtTokenService;
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDto> list() {
        return userService.listAll();
    }

    @PostMapping("/authenticate")
    public ResponseEntity<JwtAuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request) {
        return userService.authenticate(request.identifier(), request.password())
                .map(jwtTokenService::issueToken)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.status(HttpStatus.UNAUTHORIZED).build());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userAuthorization.isSelf(#id, authentication.name)")
    public ResponseEntity<UserDto> get(@PathVariable("id") Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserDto> create(@RequestBody UserDto dto) {
        UserDto created = userService.create(dto);
        return ResponseEntity.created(URI.create("/api/users/" + created.getId())).body(created);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userAuthorization.isSelf(#id, authentication.name)")
    public ResponseEntity<UserDto> update(@PathVariable("id") Long id, @RequestBody UserDto dto,
                                          Authentication authentication) {
        boolean admin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
        return userService.update(id, dto, admin)
                .map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or @userAuthorization.isSelf(#id, authentication.name)")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) {
        boolean removed = userService.delete(id);
        return removed ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
