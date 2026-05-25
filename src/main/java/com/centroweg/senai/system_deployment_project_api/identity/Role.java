package com.centroweg.senai.system_deployment_project_api.identity;

/**
 * Papéis disponíveis no sistema.
 * Mantido na raiz do módulo para que outros módulos (ex: Notification)
 * possam referenciar os papéis sem violar as fronteiras do Spring Modulith.
 */
public enum Role {
    EMPLOYEE,
    APPROVER,
    STOREKEEPER,
    ADMIN
}
