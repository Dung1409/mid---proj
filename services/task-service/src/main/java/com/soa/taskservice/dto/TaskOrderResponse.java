package com.soa.taskservice.dto;

public class TaskOrderResponse {
    private String requestId;
    private String status;
    private String message;

    public TaskOrderResponse(String requestId, String status, String message) {
        this.requestId = requestId;
        this.status = status;
        this.message = message;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}