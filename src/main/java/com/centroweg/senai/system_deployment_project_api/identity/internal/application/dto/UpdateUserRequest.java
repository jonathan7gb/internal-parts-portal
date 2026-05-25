package com.centroweg.senai.system_deployment_project_api.identity.internal.application.dto;

import com.centroweg.senai.system_deployment_project_api.identity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Usado pelo ADMIN em {@code PUT /users/{id}}.
 * Troca de senha não é permitida aqui — o usuário usa {@code PUT /users/me}.
 */
public record UpdateUserRequest(
        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        String name,

        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        String email,

        @NotNull(message = "Role is required")
        Role role
) {}
