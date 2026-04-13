package com.soa.cartservice.dto;

import jakarta.validation.constraints.NotBlank;

public class CheckoutRequest {
    @NotBlank(message = "phone is required")
    private String phone;

    @NotBlank(message = "address is required")
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