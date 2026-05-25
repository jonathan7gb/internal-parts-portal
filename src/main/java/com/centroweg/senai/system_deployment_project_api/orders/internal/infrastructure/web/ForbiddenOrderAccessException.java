package com.centroweg.senai.system_deployment_project_api.orders.internal.infrastructure.web;

import java.util.UUID;

class ForbiddenOrderAccessException extends RuntimeException {

    ForbiddenOrderAccessException(UUID orderId) {
        super("Access denied to order: " + orderId);
    }
}
