package com.centroweg.senai.system_deployment_project_api.identity.internal.domain;

import com.centroweg.senai.system_deployment_project_api.identity.Role;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

/**
 * Converter JPA para mapear {@link Role} ↔ {@code identity.user_role} (PostgreSQL ENUM).
 *
 * <p>Usar {@link AttributeConverter} com {@code String} garante compatibilidade com o
 * tipo ENUM customizado do PostgreSQL sem depender de dialect-specific annotations,
 * e permite que {@code ddl-auto=validate} funcione corretamente.
 */
@Converter
public class RoleConverter implements AttributeConverter<Role, String> {

    @Override
    public String convertToDatabaseColumn(Role role) {
        return role == null ? null : role.name();
    }

    @Override
    public Role convertToEntityAttribute(String value) {
        return value == null ? null : Role.valueOf(value);
    }
}
