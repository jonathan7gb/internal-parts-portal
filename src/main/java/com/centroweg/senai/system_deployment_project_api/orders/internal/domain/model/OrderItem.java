package com.centroweg.senai.system_deployment_project_api.orders.internal.domain.model;

import java.util.UUID;

public record OrderItem(
        UUID id,
        UUID orderId,
        UUID partId,
        String partCode,
        String partName,
        int quantity) {}
