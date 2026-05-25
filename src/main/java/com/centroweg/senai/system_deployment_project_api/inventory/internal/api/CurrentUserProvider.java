package com.centroweg.senai.system_deployment_project_api.inventory.internal.api;

import java.util.UUID;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Resolve o userId do contexto de segurança.
 * Com JWT (módulo Identity), o principal é um {@link UUID}; em testes com {@code @WithMockUser},
 * usa o {@code username} como UUID em string.
 */
@Component
public class CurrentUserProvider {

    public UUID getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("Usuário não autenticado");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UUID uuid) {
            return uuid;
        }
        return UUID.fromString(authentication.getName());
    }
}
