package com.centroweg.senai.system_deployment_project_api.orders.internal.infrastructure.persistence;

import com.centroweg.senai.system_deployment_project_api.orders.internal.domain.model.Order;
import com.centroweg.senai.system_deployment_project_api.orders.internal.domain.model.OrderItem;
import java.util.List;

final class OrderPersistenceMapper {

    private OrderPersistenceMapper() {}

    static Order toDomain(OrderJpaEntity entity) {
        List<OrderItem> items = entity.getItems().stream()
                .map(i -> new OrderItem(i.getId(), entity.getId(), i.getPartId(), i.getPartCode(), i.getPartName(), i.getQuantity()))
                .toList();
        return new Order(
                entity.getId(),
                entity.getRequesterId(),
                entity.getStatus(),
                entity.getJustification(),
                entity.getRejectionNote(),
                entity.getReviewedBy(),
                entity.getReviewedAt(),
                items,
                entity.getCreatedAt(),
                entity.getUpdatedAt());
    }

    static OrderJpaEntity toEntity(Order order) {
        OrderJpaEntity entity = new OrderJpaEntity();
        entity.setId(order.id());
        entity.setRequesterId(order.requesterId());
        entity.setStatus(order.status());
        entity.setJustification(order.justification());
        entity.setRejectionNote(order.rejectionNote());
        entity.setReviewedBy(order.reviewedBy());
        entity.setReviewedAt(order.reviewedAt());
        entity.setCreatedAt(order.createdAt());
        entity.setUpdatedAt(order.updatedAt());

        List<OrderItemJpaEntity> itemEntities = order.items().stream()
                .map(item -> {
                    OrderItemJpaEntity itemEntity = new OrderItemJpaEntity();
                    itemEntity.setId(item.id());
                    itemEntity.setOrder(entity);
                    itemEntity.setPartId(item.partId());
                    itemEntity.setPartCode(item.partCode());
                    itemEntity.setPartName(item.partName());
                    itemEntity.setQuantity(item.quantity());
                    return itemEntity;
                })
                .toList();

        entity.getItems().clear();
        entity.getItems().addAll(itemEntities);
        return entity;
    }
}
