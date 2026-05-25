package com.centroweg.senai.system_deployment_project_api.identity.internal.application.dto;

import com.centroweg.senai.system_deployment_project_api.identity.Role;
import com.centroweg.senai.system_deployment_project_api.identity.internal.domain.model.User;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de resposta de usuário. Nunca expõe {@code password}.
 */
public record UserResponse(
        UUID id,
        String name,
        String email,
        Role role,
        boolean active,
        LocalDateTime createdAt
) {
    public static UserResponse from(User user) {
        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.isActive(),
                user.getCreatedAt()
        );
    }
}
