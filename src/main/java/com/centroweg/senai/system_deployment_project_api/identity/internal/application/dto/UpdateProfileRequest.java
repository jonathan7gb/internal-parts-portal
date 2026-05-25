package com.centroweg.senai.system_deployment_project_api.identity.internal.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Usado pelo próprio usuário em {@code PUT /users/me}.
 * {@code currentPassword} e {@code newPassword} são opcionais:
 * ambos devem estar presentes para efetuar a troca de senha.
 */
public record UpdateProfileRequest(
        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        String name,

        String currentPassword,

        @Size(min = 8, message = "New password must be at least 8 characters")
        String newPassword
) {}
