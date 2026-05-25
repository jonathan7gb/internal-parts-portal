package com.centroweg.senai.system_deployment_project_api.inventory.support;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

/** Simula o contexto JWT do Identity nos testes MockMvc ({@code hasAuthority}, principal = userId). */
public final class InventorySecurityPostProcessors {

    private InventorySecurityPostProcessors() {}

    public static RequestPostProcessor asAdmin() {
        return user(InventoryTestUsers.ADMIN_ID.toString()).authorities(new SimpleGrantedAuthority("ADMIN"));
    }

    public static RequestPostProcessor asAlmoxarife() {
        return user(InventoryTestUsers.ALMOXARIFE_ID.toString())
                .authorities(new SimpleGrantedAuthority("ALMOXARIFE"));
    }

    public static RequestPostProcessor asColaborador() {
        return user(InventoryTestUsers.COLABORADOR_ID.toString())
                .authorities(new SimpleGrantedAuthority("COLABORADOR"));
    }
}
