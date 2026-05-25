package com.centroweg.senai.system_deployment_project_api.orders.internal.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record OrderItemRequest(
        @NotNull UUID partId,
        @Min(1) int quantity) {}
