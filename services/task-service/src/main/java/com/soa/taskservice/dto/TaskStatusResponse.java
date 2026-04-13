package com.soa.taskservice.dto;

public class TaskStatusResponse {
    private String requestId;
    private String orderId;
    private String status;
    private String message;

    public TaskStatusResponse(String requestId, String orderId, String status, String message) {
        this.requestId = requestId;
        this.orderId = orderId;
        this.status = status;
        this.message = message;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}