package com.soa.taskservice.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class OrderItemRequest {
    @NotNull(message = "Item id is required")
    @Min(value = 1, message = "Item id must be greater than 0")
    private Integer id;

    @NotBlank(message = "Item name is required")
    private String name;

    @NotNull(message = "Item price is required")
    private BigDecimal price;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }
}