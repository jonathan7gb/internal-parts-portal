package com.centroweg.senai.system_deployment_project_api.orders;

import java.util.List;
import java.util.UUID;

public record PedidoCriadoEvent(UUID orderId, UUID requesterId, List<ItemRef> items) {}
