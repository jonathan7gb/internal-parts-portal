package com.centroweg.senai.system_deployment_project_api.identity.internal.domain;

import com.centroweg.senai.system_deployment_project_api.identity.Role;
import com.centroweg.senai.system_deployment_project_api.identity.internal.domain.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    /** Usado por {@code UserDetailsPortImpl.findEmailsByRole} para o módulo Notification. */
    List<User> findAllByRole(Role role);

    boolean existsByEmail(String email);

    /** Verificação de unicidade de email excluindo o próprio usuário (para edição). */
    boolean existsByEmailAndIdNot(String email, UUID id);
}
