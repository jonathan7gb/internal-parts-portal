package com.centroweg.senai.system_deployment_project_api.orders.internal.infrastructure.persistence;

import com.centroweg.senai.system_deployment_project_api.orders.internal.domain.model.OrderStatus;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

interface OrderJpaRepository extends JpaRepository<OrderJpaEntity, UUID> {

    List<OrderJpaEntity> findByRequesterIdOrderByCreatedAtDesc(UUID requesterId);

    List<OrderJpaEntity> findByStatusOrderByCreatedAtAsc(OrderStatus status);
}
