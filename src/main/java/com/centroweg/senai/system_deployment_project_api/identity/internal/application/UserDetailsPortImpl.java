package com.centroweg.senai.system_deployment_project_api.identity.internal.application;

import com.centroweg.senai.system_deployment_project_api.identity.Role;
import com.centroweg.senai.system_deployment_project_api.identity.UserDetailsPort;
import com.centroweg.senai.system_deployment_project_api.identity.internal.application.service.AuthService;
import com.centroweg.senai.system_deployment_project_api.identity.internal.domain.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementação da API pública do módulo Identity.
 *
 * <p>Dois contratos implementados:
 * <ol>
 *   <li>{@code loadUserByUsername} — usado pelo Spring Security e por {@link AuthService} no login.</li>
 *   <li>{@code findEmailsByRole} — usado pelo módulo Notification para buscar destinatários de email.</li>
 * </ol>
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
class UserDetailsPortImpl implements UserDetailsPort {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + email));
    }

    @Override
    public List<String> findEmailsByRole(Role role) {
        return userRepository.findAllByRole(role)
                .stream()
                .filter(user -> user.isActive())
                .map(user -> user.getEmail())
                .toList();
    }
}
