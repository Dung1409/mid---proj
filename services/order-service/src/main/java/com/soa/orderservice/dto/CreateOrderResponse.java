package com.soa.orderservice.dto;

public class CreateOrderResponse {
    private String orderId;
    private String status;

    public CreateOrderResponse(String orderId, String status) {
        this.orderId = orderId;
        this.status = status;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getStatus() {
        return status;
    }
}