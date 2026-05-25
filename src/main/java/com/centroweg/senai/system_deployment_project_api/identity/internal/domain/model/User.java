package com.centroweg.senai.system_deployment_project_api.identity.internal.domain.model;

import com.centroweg.senai.system_deployment_project_api.identity.Role;
import com.centroweg.senai.system_deployment_project_api.identity.internal.domain.RoleConverter;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

/**
 * Entidade de usuário mapeada para {@code identity.users}.
 *
 * <p>Implementa {@link UserDetails} para integração direta com Spring Security,
 * evitando uma classe adaptadora intermediária.
 *
 * <p>Regras de mutabilidade:
 * <ul>
 *   <li>{@code id}, {@code email} (usado como username), {@code createdAt} são imutáveis após criação.</li>
 *   <li>{@code email} pode ser alterado apenas pelo ADMIN via {@code PUT /users/{id}}.</li>
 *   <li>{@code name}, {@code password}, {@code role} e {@code active} têm setters explícitos.</li>
 * </ul>
 */
@Entity
@Table(schema = "identity", name = "users")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(updatable = false, nullable = false)
    private UUID id;

    @Setter
    @Column(nullable = false, length = 100)
    private String name;

    @Setter
    @Column(unique = true, nullable = false, length = 150)
    private String email;

    /** Hash bcrypt — nunca expor em responses. */
    @Setter
    @Column(nullable = false, length = 255)
    private String password;

    @Setter
    @Convert(converter = RoleConverter.class)
    @Column(name = "role", nullable = false)
    private Role role;

    @Setter
    @Column(nullable = false)
    private boolean active;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    void onPersist() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // -------------------------------------------------------------------------
    // UserDetails — delegação ao campo active e role
    // -------------------------------------------------------------------------

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    /** Spring Security usa email como username. */
    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return active;
    }
}
