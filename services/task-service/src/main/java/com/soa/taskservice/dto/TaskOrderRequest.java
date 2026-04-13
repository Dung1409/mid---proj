package com.soa.taskservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;

public class TaskOrderRequest {
    @NotBlank(message = "phone must not be empty")
    private String phone;

    @NotBlank(message = "address must not be empty")
    private String address;

    @NotEmpty(message = "items must contain at least 1 item")
    private List<@Valid OrderItemRequest> items;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public List<OrderItemRequest> getItems() {
        return items;
    }

    public void setItems(List<OrderItemRequest> items) {
        this.items = items;
    }
}