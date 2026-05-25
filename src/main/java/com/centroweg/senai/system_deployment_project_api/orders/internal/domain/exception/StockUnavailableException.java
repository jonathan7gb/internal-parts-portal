package com.centroweg.senai.system_deployment_project_api.orders.internal.domain.exception;

import java.util.List;
import java.util.UUID;

public class StockUnavailableException extends RuntimeException {

    private final List<UnavailableItem> unavailableItems;

    public StockUnavailableException(List<UnavailableItem> unavailableItems) {
        super("Stock unavailable for one or more items");
        this.unavailableItems = unavailableItems;
    }

    public List<UnavailableItem> getUnavailableItems() {
        return unavailableItems;
    }

    public record UnavailableItem(UUID partId, String partCode, int requested, int available) {}
}
