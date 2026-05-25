package com.centroweg.senai.system_deployment_project_api.orders.internal.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RejectOrderRequest(
        @NotBlank @Size(min = 10) String rejectionNote) {}
