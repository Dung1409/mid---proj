package com.soa.cartservice.dto;

public class CheckoutResponse {
    private String requestId;
    private String message;

    public CheckoutResponse(String requestId, String message) {
        this.requestId = requestId;
        this.message = message;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getMessage() {
        return message;
    }
}