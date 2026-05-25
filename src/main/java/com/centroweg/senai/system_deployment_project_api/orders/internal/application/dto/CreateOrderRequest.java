package com.centroweg.senai.system_deployment_project_api.orders.internal.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

public record CreateOrderRequest(
        @NotBlank @Size(min = 10) String justification,
        @NotEmpty @Valid List<OrderItemRequest> items) {}
