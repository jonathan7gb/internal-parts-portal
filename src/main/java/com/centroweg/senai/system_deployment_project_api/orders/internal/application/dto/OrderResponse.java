package com.centroweg.senai.system_deployment_project_api.orders.internal.application.dto;

import com.centroweg.senai.system_deployment_project_api.orders.internal.domain.model.Order;
import com.centroweg.senai.system_deployment_project_api.orders.internal.domain.model.OrderStatus;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderResponse(
        UUID id,
        UUID requesterId,
        OrderStatus status,
        String justification,
        String rejectionNote,
        UUID reviewedBy,
        Instant reviewedAt,
        Instant createdAt,
        Instant updatedAt,
        List<OrderItemResponse> items) {

    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.id(),
                order.requesterId(),
                order.status(),
                order.justification(),
                order.rejectionNote(),
                order.reviewedBy(),
                order.reviewedAt(),
                order.createdAt(),
                order.updatedAt(),
                order.items().stream().map(OrderItemResponse::from).toList());
    }
}
