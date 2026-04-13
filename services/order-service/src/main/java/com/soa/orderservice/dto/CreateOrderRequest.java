package com.soa.orderservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class CreateOrderRequest {
    @NotEmpty(message = "items must not be empty")
    private List<Map<String, Object>> items;

    @NotNull(message = "total is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "total must be greater than 0")
    private BigDecimal total;

    public List<Map<String, Object>> getItems() {
        return items;
    }

    public void setItems(List<Map<String, Object>> items) {
        this.items = items;
    }

    public BigDecimal getTotal() {
        return total;
    }

    public void setTotal(BigDecimal total) {
        this.total = total;
    }
}