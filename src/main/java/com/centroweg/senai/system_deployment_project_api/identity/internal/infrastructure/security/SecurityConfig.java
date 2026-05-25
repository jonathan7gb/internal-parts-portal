package com.centroweg.senai.system_deployment_project_api.identity.internal.infrastructure.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.Map;

/**
 * Configuração central de segurança da aplicação.
 *
 * <p>Estratégia: stateless JWT — sem sessão HTTP, sem CSRF.
 * Toda autenticação passa pelo {@link JwtAuthFilter} antes dos controllers.
 *
 * <p>{@code @EnableMethodSecurity} habilita {@code @PreAuthorize} nos controllers.
 * Roles sem prefixo "ROLE_" — usar {@code hasAuthority('ADMIN')}, não {@code hasRole('ADMIN')}.
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;
    private final ObjectMapper objectMapper;

    @Bean
    SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/auth/login").permitAll()
                        .requestMatchers("/actuator/health").permitAll()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((request, response, e) -> {
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setStatus(HttpStatus.UNAUTHORIZED.value());
                            objectMapper.writeValue(response.getWriter(), Map.of(
                                    "status", 401,
                                    "error", "Unauthorized",
                                    "message", "Authentication required",
                                    "timestamp", Instant.now().toString()
                            ));
                        })
                        .accessDeniedHandler((request, response, e) -> {
                            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                            response.setStatus(HttpStatus.FORBIDDEN.value());
                            objectMapper.writeValue(response.getWriter(), Map.of(
                                    "status", 403,
                                    "error", "Forbidden",
                                    "message", "Access denied",
                                    "timestamp", Instant.now().toString()
                            ));
                        })
                )
                .build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
