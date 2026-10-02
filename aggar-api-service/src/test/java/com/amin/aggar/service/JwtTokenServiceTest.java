package com.amin.aggar.service;

import com.amin.aggar.api.dto.AuthenticatedUserDto;
import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.junit.jupiter.api.Test;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtTokenServiceTest {

    @Test
    void issuesSignedTokenWithUserIdAndAdminRole() {
        SecretKey key = new SecretKeySpec(
                "test-secret-with-at-least-thirty-two-bytes".getBytes(StandardCharsets.UTF_8),
                "HmacSHA256");
        JwtEncoder encoder = new NimbusJwtEncoder(new ImmutableSecret<>(key));
        JwtDecoder decoder = NimbusJwtDecoder.withSecretKey(key)
                .macAlgorithm(MacAlgorithm.HS256)
                .build();
        JwtTokenService tokenService = new JwtTokenService(encoder, Duration.ofMinutes(30));

        var response = tokenService.issueToken(new AuthenticatedUserDto(
                42L, "site-admin", "Admin", "admin@example.com", null, "admin", LocalDateTime.now()));
        var decoded = decoder.decode(response.accessToken());

        assertEquals("site-admin", decoded.getSubject());
        assertEquals(42L, ((Number) decoded.getClaim("userId")).longValue());
        assertEquals("ROLE_ADMIN", decoded.getClaimAsStringList("roles").get(0));
        assertEquals("Bearer", response.tokenType());
        assertTrue(response.expiresAt().isAfter(decoded.getIssuedAt()));
    }
}
