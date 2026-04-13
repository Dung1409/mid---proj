package com.soa.orderservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "orders")
public class OrderEntity {
    @Id
    private String orderId;

    @Column(nullable = false)
    private String status;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal total;

    @Lob
    @Column(nullable = false)
    private String itemsJson;

    public OrderEntity() {
    }

    public OrderEntity(String orderId, String status, BigDecimal total, String itemsJson) {
        this.orderId = orderId;
        this.status = status;
        this.total = total;
        this.itemsJson = itemsJson;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}