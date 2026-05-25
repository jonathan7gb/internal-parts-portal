package com.centroweg.senai.system_deployment_project_api.identity.internal.application.service;

import com.centroweg.senai.system_deployment_project_api.identity.Role;
import com.centroweg.senai.system_deployment_project_api.identity.internal.domain.model.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

/**
 * Responsável exclusivamente por geração e validação de JWT.
 *
 * <p>Payload mínimo: {@code sub} (userId), {@code role}, {@code iat}, {@code exp}.
 * Não conhece a entidade {@link User}
 * — trabalha apenas com primitivos/UUIDs para manter o acoplamento mínimo.
 */
@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expirationMs;

    private SecretKey signingKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generate(UUID userId, Role role) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(expirationMs);

        return Jwts.builder()
                .subject(userId.toString())
                .claim("role", role.name())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(signingKey())
                .compact();
    }

    public Claims parse(String token) {
        return Jwts.parser()
                .verifyWith(signingKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public UUID extractUserId(String token) {
        return UUID.fromString(parse(token).getSubject());
    }

    public Role extractRole(String token) {
        return Role.valueOf(parse(token).get("role", String.class));
    }

    public Instant extractExpiration(String token) {
        return parse(token).getExpiration().toInstant();
    }
}
