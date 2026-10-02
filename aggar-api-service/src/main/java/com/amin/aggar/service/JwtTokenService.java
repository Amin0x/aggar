package com.amin.aggar.service;

import com.amin.aggar.api.dto.AuthenticatedUserDto;
import com.amin.aggar.api.dto.JwtAuthenticationResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Locale;

@Service
public class JwtTokenService {

    private final JwtEncoder jwtEncoder;
    private final Duration tokenTtl;

    public JwtTokenService(
            JwtEncoder jwtEncoder,
            @Value("${app.security.jwt.expiration:PT30M}") Duration tokenTtl) {
        this.jwtEncoder = jwtEncoder;
        this.tokenTtl = tokenTtl;
    }

    public JwtAuthenticationResponse issueToken(AuthenticatedUserDto user) {
        Instant issuedAt = Instant.now();
        Instant expiresAt = issuedAt.plus(tokenTtl);
        String role = user.role() == null ? "USER" : user.role().toUpperCase(Locale.ROOT);
        if (role.startsWith("ROLE_")) {
            role = role.substring("ROLE_".length());
        }
        String token = jwtEncoder.encode(JwtEncoderParameters.from(
                JwsHeader.with(MacAlgorithm.HS256).build(),
                JwtClaimsSet.builder()
                        .issuer("aggar-api")
                        .subject(user.username())
                        .issuedAt(issuedAt)
                        .expiresAt(expiresAt)
                        .claim("userId", user.id())
                        .claim("roles", List.of("ROLE_" + role))
                        .build())).getTokenValue();

        return new JwtAuthenticationResponse(
                user.id(), user.username(), user.name(), user.email(), user.phone(), user.role(),
                user.createdAt(), token, "Bearer", expiresAt);
    }
}
