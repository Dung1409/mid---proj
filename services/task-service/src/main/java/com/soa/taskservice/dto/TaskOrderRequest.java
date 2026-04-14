package com.soa.taskservice.dto;

import jakarta.validation.constraints.NotBlank;

public class TaskOrderRequest {
    @NotBlank(message = "phone must not be empty")
    private String phone;

    @NotBlank(message = "address must not be empty")
    private String address;

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
}