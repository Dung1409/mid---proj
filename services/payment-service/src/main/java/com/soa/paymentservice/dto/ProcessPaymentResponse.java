package com.soa.paymentservice.dto;

public class ProcessPaymentResponse {
    private String status;

    public ProcessPaymentResponse(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }
}