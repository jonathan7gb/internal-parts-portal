package com.centroweg.senai.system_deployment_project_api.identity.internal.infrastructure.web.controller;

import com.centroweg.senai.system_deployment_project_api.identity.internal.application.dto.LoginRequest;
import com.centroweg.senai.system_deployment_project_api.identity.internal.application.dto.LoginResponse;
import com.centroweg.senai.system_deployment_project_api.identity.internal.application.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Endpoints de autenticação — públicos, sem JWT.
 *
 * <pre>
 * POST /auth/login  →  200 LoginResponse | 401
 * </pre>
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}
