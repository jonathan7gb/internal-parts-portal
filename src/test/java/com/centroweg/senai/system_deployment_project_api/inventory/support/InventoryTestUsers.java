package com.centroweg.senai.system_deployment_project_api.inventory.support;

import java.util.UUID;

/** IDs fixos para testes — alinhados ao principal UUID do JWT do módulo Identity. */
public final class InventoryTestUsers {

    public static final UUID ADMIN_ID = UUID.fromString("00000000-0000-0000-0000-000000000001");
    public static final UUID ALMOXARIFE_ID = UUID.fromString("00000000-0000-0000-0000-000000000002");
    public static final UUID COLABORADOR_ID = UUID.fromString("00000000-0000-0000-0000-000000000003");

    private InventoryTestUsers() {}
}
