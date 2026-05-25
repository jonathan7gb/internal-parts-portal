package com.centroweg.senai.system_deployment_project_api.orders.internal.application;

import com.centroweg.senai.system_deployment_project_api.inventory.StockCheckPort;
import com.centroweg.senai.system_deployment_project_api.inventory.StockCheckResult;
import com.centroweg.senai.system_deployment_project_api.orders.PedidoAprovadoEvent;
import com.centroweg.senai.system_deployment_project_api.orders.PedidoConcluidoEvent;
import com.centroweg.senai.system_deployment_project_api.orders.PedidoCriadoEvent;
import com.centroweg.senai.system_deployment_project_api.orders.PedidoRejeitadoEvent;
import com.centroweg.senai.system_deployment_project_api.orders.internal.application.dto.CreateOrderRequest;
import com.centroweg.senai.system_deployment_project_api.orders.internal.application.dto.OrderItemRequest;
import com.centroweg.senai.system_deployment_project_api.orders.internal.domain.exception.OrderNotFoundException;
import com.centroweg.senai.system_deployment_project_api.orders.internal.domain.exception.StockUnavailableException;
import com.centroweg.senai.system_deployment_project_api.orders.internal.domain.exception.StockUnavailableException.UnavailableItem;
import java.util.ArrayList;
import com.centroweg.senai.system_deployment_project_api.orders.internal.domain.model.Order;
import com.centroweg.senai.system_deployment_project_api.orders.internal.domain.model.OrderItem;
import com.centroweg.senai.system_deployment_project_api.orders.internal.domain.model.OrderStatus;
import com.centroweg.senai.system_deployment_project_api.orders.internal.domain.repository.OrderRepository;
import java.time.Instant;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class OrderService {

    private final OrderRepository orderRepository;
    private final StockCheckPort stockCheckPort;
    private final ApplicationEventPublisher eventPublisher;

    public OrderService(
            OrderRepository orderRepository,
            StockCheckPort stockCheckPort,
            ApplicationEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.stockCheckPort = stockCheckPort;
        this.eventPublisher = eventPublisher;
    }

    public Order createOrder(UUID requesterId, CreateOrderRequest request) {
        List<StockCheckEntry> entries = checkAllItems(request.items());
        List<UnavailableItem> unavailable = collectUnavailable(entries);
        if (!unavailable.isEmpty()) {
            throw new StockUnavailableException(unavailable);
        }

        List<OrderItem> items = buildItems(entries);
        Instant now = Instant.now();
        Order order = new Order(
                UUID.randomUUID(), requesterId, OrderStatus.PENDENTE,
                request.justification(), null, null, null, items, now, now);

        Order saved = orderRepository.save(order);
        eventPublisher.publishEvent(new PedidoCriadoEvent(saved.id(), requesterId, saved.toItemRefs()));
        return saved;
    }

    @Transactional(readOnly = true)
    public List<Order> listByRequester(UUID requesterId) {
        return orderRepository.findByRequesterId(requesterId);
    }

    @Transactional(readOnly = true)
    public Order getById(UUID id) {
        return orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Order> listPending() {
        return orderRepository.findByStatus(OrderStatus.PENDENTE);
    }

    @Transactional(readOnly = true)
    public List<Order> listApproved() {
        return orderRepository.findByStatus(OrderStatus.APROVADO);
    }

    public Order approve(UUID orderId, UUID reviewerId) {
        Order order = getById(orderId);
        Order approved = orderRepository.save(order.approve(reviewerId));
        eventPublisher.publishEvent(new PedidoAprovadoEvent(approved.id(), reviewerId, approved.toItemRefs()));
        return approved;
    }

    public Order reject(UUID orderId, UUID reviewerId, String rejectionNote) {
        Order order = getById(orderId);
        Order rejected = orderRepository.save(order.reject(reviewerId, rejectionNote));
        eventPublisher.publishEvent(
                new PedidoRejeitadoEvent(rejected.id(), reviewerId, rejectionNote, rejected.toItemRefs()));
        return rejected;
    }

    public Order complete(UUID orderId, UUID almoxarifeId) {
        Order order = getById(orderId);
        Order completed = orderRepository.save(order.complete(almoxarifeId));
        eventPublisher.publishEvent(
                new PedidoConcluidoEvent(completed.id(), almoxarifeId, completed.toItemRefs()));
        return completed;
    }

    private record StockCheckEntry(OrderItemRequest request, StockCheckResult result) {}

    private List<StockCheckEntry> checkAllItems(List<OrderItemRequest> items) {
        return items.stream()
                .map(item -> new StockCheckEntry(item, stockCheckPort.checkAvailability(item.partId(), item.quantity())))
                .toList();
    }

    private List<UnavailableItem> collectUnavailable(List<StockCheckEntry> entries) {
        return entries.stream()
                .filter(e -> !e.result().available())
                .map(e -> new UnavailableItem(e.request().partId(), null, e.request().quantity(), e.result().qtyAvailable()))
                .toList();
    }

    private List<OrderItem> buildItems(List<StockCheckEntry> entries) {
        // partCode and partName are not available via StockCheckPort; stored as empty until
        // inventory exposes a snapshot method or Identity module enriches the request.
        return entries.stream()
                .map(e -> new OrderItem(
                        UUID.randomUUID(), null, e.request().partId(), "", "", e.request().quantity()))
                .toList();
    }
}
