package com.centroweg.senai.system_deployment_project_api.orders.internal.infrastructure.persistence;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "ord_order_items")
@Getter
@Setter
@NoArgsConstructor
class OrderItemJpaEntity {

    @Id
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private OrderJpaEntity order;

    @Column(name = "part_id", nullable = false)
    private UUID partId;

    @Column(name = "part_code", nullable = false, length = 50)
    private String partCode;

    @Column(name = "part_name", nullable = false, length = 150)
    private String partName;

    @Column(nullable = false)
    private int quantity;
}
