package com.centroweg.senai.system_deployment_project_api.orders.internal.infrastructure.persistence;

import com.centroweg.senai.system_deployment_project_api.orders.internal.domain.model.Order;
import com.centroweg.senai.system_deployment_project_api.orders.internal.domain.model.OrderStatus;
import com.centroweg.senai.system_deployment_project_api.orders.internal.domain.repository.OrderRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
class OrderRepositoryAdapter implements OrderRepository {

    private final OrderJpaRepository jpaRepository;

    OrderRepositoryAdapter(OrderJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Order save(Order order) {
        return OrderPersistenceMapper.toDomain(jpaRepository.save(OrderPersistenceMapper.toEntity(order)));
    }

    @Override
    public Optional<Order> findById(UUID id) {
        return jpaRepository.findById(id).map(OrderPersistenceMapper::toDomain);
    }

    @Override
    public List<Order> findByRequesterId(UUID requesterId) {
        return jpaRepository.findByRequesterIdOrderByCreatedAtDesc(requesterId).stream()
                .map(OrderPersistenceMapper::toDomain)
                .toList();
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        return jpaRepository.findByStatusOrderByCreatedAtAsc(status).stream()
                .map(OrderPersistenceMapper::toDomain)
                .toList();
    }
}
