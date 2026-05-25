package com.centroweg.senai.system_deployment_project_api.identity.internal.infrastructure.web.controller;

import com.centroweg.senai.system_deployment_project_api.identity.internal.application.dto.*;
import com.centroweg.senai.system_deployment_project_api.identity.internal.application.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Endpoints de gerenciamento de usuários.
 *
 * <p>Roles aplicadas via {@code @PreAuthorize} (nunca dentro dos services).
 * {@code principal} no contexto de segurança é um {@link UUID} (userId extraído do JWT).
 *
 * <pre>
 * GET    /users                  → ADMIN
 * POST   /users                  → ADMIN
 * PUT    /users/{id}             → ADMIN
 * PATCH  /users/{id}/deactivate  → ADMIN
 * GET    /users/me               → qualquer autenticado
 * PUT    /users/me               → qualquer autenticado
 * </pre>
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
class UserController {

    private final UserService userService;

    // -------------------------------------------------------------------------
    // Endpoints ADMIN
    // -------------------------------------------------------------------------

    @GetMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    ResponseEntity<List<UserResponse>> listAll() {
        return ResponseEntity.ok(userService.listAll());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('ADMIN')")
    ResponseEntity<UserResponse> create(@RequestBody @Valid CreateUserRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    ResponseEntity<UserResponse> update(
            @PathVariable UUID id,
            @RequestBody @Valid UpdateUserRequest request
    ) {
        return ResponseEntity.ok(userService.update(id, request));
    }

    @PatchMapping("/{id}/deactivate")
    @PreAuthorize("hasAuthority('ADMIN')")
    ResponseEntity<Void> deactivate(@PathVariable UUID id, Authentication auth) {
        UUID authenticatedUserId = (UUID) auth.getPrincipal();
        userService.deactivate(id, authenticatedUserId);
        return ResponseEntity.noContent().build();
    }

    // -------------------------------------------------------------------------
    // Endpoints perfil próprio — qualquer usuário autenticado
    // -------------------------------------------------------------------------

    @GetMapping("/me")
    ResponseEntity<UserResponse> getMe(Authentication auth) {
        UUID userId = (UUID) auth.getPrincipal();
        return ResponseEntity.ok(userService.getProfile(userId));
    }

    @PutMapping("/me")
    ResponseEntity<UserResponse> updateMe(
            @RequestBody @Valid UpdateProfileRequest request,
            Authentication auth
    ) {
        UUID userId = (UUID) auth.getPrincipal();
        return ResponseEntity.ok(userService.updateProfile(userId, request));
    }
}
