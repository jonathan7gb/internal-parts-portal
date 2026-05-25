package com.centroweg.senai.system_deployment_project_api.identity;

import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

/**
 * API pública do módulo Identity.
 *
 * <p>Exposta na raiz do pacote para que outros módulos possam injetá-la
 * sem violar as fronteiras do Spring Modulith.
 *
 * <p>Usos esperados por módulo:
 * <ul>
 *   <li><b>Notification</b>: {@code findEmailsByRole} para buscar destinatários de email por papel</li>
 *   <li><b>Spring Security</b>: {@code loadUserByUsername} via {@link UserDetailsService}</li>
 * </ul>
 */
public interface UserDetailsPort extends UserDetailsService {

    /**
     * Retorna a lista de emails de todos os usuários ativos com o papel informado.
     * Usado pelo módulo Notification para encontrar destinatários de emails.
     *
     * @param role papel a filtrar
     * @return lista de emails (nunca nula, pode ser vazia)
     */
    List<String> findEmailsByRole(Role role);
}
