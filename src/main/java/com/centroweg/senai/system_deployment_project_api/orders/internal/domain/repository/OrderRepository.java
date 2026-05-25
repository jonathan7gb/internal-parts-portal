package com.centroweg.senai.system_deployment_project_api.orders.internal.domain.repository;

import com.centroweg.senai.system_deployment_project_api.orders.internal.domain.model.Order;
import com.centroweg.senai.system_deployment_project_api.orders.internal.domain.model.OrderStatus;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(UUID id);

    List<Order> findByRequesterId(UUID requesterId);

    List<Order> findByStatus(OrderStatus status);
}
