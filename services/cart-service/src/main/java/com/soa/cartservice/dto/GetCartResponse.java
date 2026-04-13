package com.soa.cartservice.dto;

import java.math.BigDecimal;
import java.util.List;

public class GetCartResponse {
    private List<CartItemResponse> items;
    private BigDecimal total;

    public GetCartResponse(List<CartItemResponse> items, BigDecimal total) {
        this.items = items;
        this.total = total;
    }

    public List<CartItemResponse> getItems() {
        return items;
    }

    public BigDecimal getTotal() {
        return total;
    }
}