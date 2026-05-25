package com.centroweg.senai.system_deployment_project_api.orders.internal.domain.model;

import com.centroweg.senai.system_deployment_project_api.orders.ItemRef;
import com.centroweg.senai.system_deployment_project_api.orders.internal.domain.exception.InvalidOrderStateException;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record Order(
        UUID id,
        UUID requesterId,
        OrderStatus status,
        String justification,
        String rejectionNote,
        UUID reviewedBy,
        Instant reviewedAt,
        List<OrderItem> items,
        Instant createdAt,
        Instant updatedAt) {

    public Order approve(UUID reviewerId) {
        if (status != OrderStatus.PENDENTE) {
            throw new InvalidOrderStateException(id, status, OrderStatus.APROVADO);
        }
        return new Order(
                id, requesterId, OrderStatus.APROVADO, justification,
                null, reviewerId, Instant.now(), items, createdAt, Instant.now());
    }

    public Order reject(UUID reviewerId, String rejectionNote) {
        if (status != OrderStatus.PENDENTE) {
            throw new InvalidOrderStateException(id, status, OrderStatus.REJEITADO);
        }
        return new Order(
                id, requesterId, OrderStatus.REJEITADO, justification,
                rejectionNote, reviewerId, Instant.now(), items, createdAt, Instant.now());
    }

    public Order complete(UUID almoxarifeId) {
        if (status != OrderStatus.APROVADO) {
            throw new InvalidOrderStateException(id, status, OrderStatus.CONCLUIDO);
        }
        return new Order(
                id, requesterId, OrderStatus.CONCLUIDO, justification,
                rejectionNote, almoxarifeId, Instant.now(), items, createdAt, Instant.now());
    }

    public List<ItemRef> toItemRefs() {
        return items.stream()
                .map(item -> new ItemRef(item.partId(), item.quantity()))
                .toList();
    }
}
