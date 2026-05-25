package com.centroweg.senai.system_deployment_project_api.orders.internal.application.dto;

import com.centroweg.senai.system_deployment_project_api.orders.internal.domain.model.OrderItem;
import java.util.UUID;

public record OrderItemResponse(
        UUID id,
        UUID partId,
        String partCode,
        String partName,
        int quantity) {

    public static OrderItemResponse from(OrderItem item) {
        return new OrderItemResponse(
                item.id(), item.partId(), item.partCode(), item.partName(), item.quantity());
    }
}
