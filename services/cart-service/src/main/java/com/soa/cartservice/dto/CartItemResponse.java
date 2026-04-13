package com.soa.cartservice.dto;

import java.math.BigDecimal;

public class CartItemResponse {
    private Integer itemId;
    private String name;
    private BigDecimal price;
    private Integer quantity;

    public CartItemResponse(Integer itemId, String name, BigDecimal price, Integer quantity) {
        this.itemId = itemId;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
    }

    public Integer getItemId() {
        return itemId;
    }

    public String getName() {
        return name;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Integer getQuantity() {
        return quantity;
    }
}