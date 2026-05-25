package com.centroweg.senai.system_deployment_project_api.orders.internal.infrastructure.web;

import com.centroweg.senai.system_deployment_project_api.orders.internal.application.OrderService;
import com.centroweg.senai.system_deployment_project_api.orders.internal.application.dto.CreateOrderRequest;
import com.centroweg.senai.system_deployment_project_api.orders.internal.application.dto.OrderResponse;
import com.centroweg.senai.system_deployment_project_api.orders.internal.application.dto.RejectOrderRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
@Tag(name = "Orders", description = "Order lifecycle management")
class OrderController {

    private final OrderService orderService;
    private final CurrentUserProvider currentUserProvider;

    OrderController(OrderService orderService, CurrentUserProvider currentUserProvider) {
        this.orderService = orderService;
        this.currentUserProvider = currentUserProvider;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('COLABORADOR')")
    @Operation(summary = "Create order", description = "Validates stock synchronously before persisting. Publishes PedidoCriadoEvent.")
    @ApiResponse(responseCode = "201", description = "Order created")
    @ApiResponse(responseCode = "422", description = "Stock unavailable for one or more items")
    OrderResponse create(@Valid @RequestBody CreateOrderRequest request) {
        return OrderResponse.from(orderService.createOrder(currentUserProvider.getCurrentUserId(), request));
    }

    @GetMapping
    @PreAuthorize("hasRole('COLABORADOR')")
    @Operation(summary = "List own orders")
    @ApiResponse(responseCode = "200", description = "Paginated list of requester's orders")
    List<OrderResponse> listMine() {
        return orderService.listByRequester(currentUserProvider.getCurrentUserId()).stream()
                .map(OrderResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('COLABORADOR', 'APROVADOR', 'ALMOXARIFE', 'ADMIN')")
    @Operation(summary = "Get order by id")
    @ApiResponse(responseCode = "200", description = "Order found")
    @ApiResponse(responseCode = "403", description = "COLABORADOR accessing another user's order")
    @ApiResponse(responseCode = "404", description = "Order not found")
    OrderResponse getById(@PathVariable UUID id) {
        UUID currentUserId = currentUserProvider.getCurrentUserId();
        var order = orderService.getById(id);

        boolean isColaborador = SecurityContextHelper.hasRole("COLABORADOR");
        if (isColaborador && !order.requesterId().equals(currentUserId)) {
            throw new ForbiddenOrderAccessException(id);
        }

        return OrderResponse.from(order);
    }

    @GetMapping("/pending")
    @PreAuthorize("hasRole('APROVADOR')")
    @Operation(summary = "List pending orders queue")
    @ApiResponse(responseCode = "200", description = "List of PENDENTE orders")
    List<OrderResponse> listPending() {
        return orderService.listPending().stream()
                .map(OrderResponse::from)
                .toList();
    }

    @PostMapping("/{id}/approve")
    @PreAuthorize("hasRole('APROVADOR')")
    @Operation(summary = "Approve order", description = "Publishes PedidoAprovadoEvent.")
    @ApiResponse(responseCode = "200", description = "Order approved")
    @ApiResponse(responseCode = "409", description = "Order is not PENDENTE")
    OrderResponse approve(@PathVariable UUID id) {
        return OrderResponse.from(orderService.approve(id, currentUserProvider.getCurrentUserId()));
    }

    @PostMapping("/{id}/reject")
    @PreAuthorize("hasRole('APROVADOR')")
    @Operation(summary = "Reject order", description = "Requires rejectionNote. Publishes PedidoRejeitadoEvent.")
    @ApiResponse(responseCode = "200", description = "Order rejected")
    @ApiResponse(responseCode = "409", description = "Order is not PENDENTE")
    OrderResponse reject(@PathVariable UUID id, @Valid @RequestBody RejectOrderRequest request) {
        return OrderResponse.from(
                orderService.reject(id, currentUserProvider.getCurrentUserId(), request.rejectionNote()));
    }

    @GetMapping("/approved")
    @PreAuthorize("hasRole('ALMOXARIFE')")
    @Operation(summary = "List approved orders awaiting fulfillment")
    @ApiResponse(responseCode = "200", description = "List of APROVADO orders")
    List<OrderResponse> listApproved() {
        return orderService.listApproved().stream()
                .map(OrderResponse::from)
                .toList();
    }

    @PostMapping("/{id}/complete")
    @PreAuthorize("hasRole('ALMOXARIFE')")
    @Operation(summary = "Complete order", description = "Publishes PedidoConcluidoEvent.")
    @ApiResponse(responseCode = "200", description = "Order completed")
    @ApiResponse(responseCode = "409", description = "Order is not APROVADO")
    OrderResponse complete(@PathVariable UUID id) {
        return OrderResponse.from(orderService.complete(id, currentUserProvider.getCurrentUserId()));
    }
}
