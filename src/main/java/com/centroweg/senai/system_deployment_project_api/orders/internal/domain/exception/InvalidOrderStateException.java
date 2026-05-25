package com.centroweg.senai.system_deployment_project_api.orders.internal.domain.exception;

import com.centroweg.senai.system_deployment_project_api.orders.internal.domain.model.OrderStatus;
import java.util.UUID;

public class InvalidOrderStateException extends RuntimeException {

    public InvalidOrderStateException(UUID orderId, OrderStatus current, OrderStatus attempted) {
        super("Order %s cannot transition from %s to %s".formatted(orderId, current, attempted));
    }
}
