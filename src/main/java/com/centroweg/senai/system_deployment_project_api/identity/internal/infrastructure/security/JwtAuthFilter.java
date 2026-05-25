package com.centroweg.senai.system_deployment_project_api.identity.internal.infrastructure.security;

import com.centroweg.senai.system_deployment_project_api.identity.Role;
import com.centroweg.senai.system_deployment_project_api.identity.internal.application.service.JwtService;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

/**
 * Filtro JWT — intercepta cada requisição uma única vez.
 *
 * <p>Extrai o token do header {@code Authorization: Bearer <token>}, valida a assinatura
 * e popula o {@link SecurityContextHolder} com o userId (como {@code principal}) e o papel.
 *
 * <p>Em caso de token ausente ou inválido, o filtro NÃO interrompe a cadeia — deixa o
 * {@link SecurityConfig} rejeitar a requisição com {@code 401} via {@code AuthenticationEntryPoint}.
 */
@Component
@RequiredArgsConstructor
class JwtAuthFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (header == null || !header.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = header.substring(BEARER_PREFIX.length());

        try {
            UUID userId = jwtService.extractUserId(token);
            Role role = jwtService.extractRole(token);

            if (SecurityContextHolder.getContext().getAuthentication() == null) {
                UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                        userId,
                        null,
                        List.of(new SimpleGrantedAuthority(role.name()))
                );
                auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(auth);
            }

        } catch (JwtException | IllegalArgumentException ignored) {
            // Token inválido ou expirado — SecurityConfig retornará 401 automaticamente
        }

        filterChain.doFilter(request, response);
    }
}
