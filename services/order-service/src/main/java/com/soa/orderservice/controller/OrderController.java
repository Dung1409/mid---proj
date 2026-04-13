package com.soa.orderservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.soa.orderservice.dto.CreateOrderRequest;
import com.soa.orderservice.dto.CreateOrderResponse;
import com.soa.orderservice.dto.UpdateOrderStatusRequest;
import com.soa.orderservice.entity.OrderEntity;
import com.soa.orderservice.repository.OrderRepository;
import jakarta.validation.Valid;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/orders")
public class OrderController {
    private final OrderRepository orderRepository;
    private final ObjectMapper objectMapper;

    public OrderController(OrderRepository orderRepository, ObjectMapper objectMapper) {
        this.orderRepository = orderRepository;
        this.objectMapper = objectMapper;
    }

    @PostMapping
    public ResponseEntity<CreateOrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        String orderId = UUID.randomUUID().toString();
        try {
            String itemsJson = objectMapper.writeValueAsString(request.getItems());
            orderRepository.save(new OrderEntity(orderId, "CREATED", request.getTotal(), itemsJson));
        } catch (JsonProcessingException ex) {
            throw new IllegalArgumentException("Invalid input data");
        }
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(new CreateOrderResponse(orderId, "CREATED"));
    }

    @PutMapping("/{orderId}/status")
    public ResponseEntity<CreateOrderResponse> updateOrderStatus(
            @PathVariable String orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        OrderEntity order = orderRepository.findById(orderId).orElse(null);
        if (order == null) {
            throw new IllegalArgumentException("Order not found");
        }

        order.setStatus(request.getStatus());
        orderRepository.save(order);
        return ResponseEntity.ok(new CreateOrderResponse(orderId, request.getStatus()));
    }
}