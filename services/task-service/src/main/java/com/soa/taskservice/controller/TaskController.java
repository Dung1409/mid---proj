package com.soa.taskservice.controller;

import com.soa.taskservice.dto.TaskOrderRequest;
import com.soa.taskservice.dto.TaskOrderResponse;
import com.soa.taskservice.dto.TaskStatusResponse;
import com.soa.taskservice.exception.RequestNotFoundException;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/task")
public class TaskController {
    private final Map<String, TaskStatusResponse> requestStatusStore = new ConcurrentHashMap<>();
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${order.service.base-url}")
    private String orderServiceBaseUrl;

    @Value("${payment.service.base-url}")
    private String paymentServiceBaseUrl;

    @PostMapping("/order")
    public ResponseEntity<TaskOrderResponse> submitOrder(@Valid @RequestBody TaskOrderRequest request) {
        BigDecimal totalPrice = request.getItems()
                .stream()
                .map(item -> item.getPrice())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        if (totalPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Invalid input data");
        }

        String requestId = UUID.randomUUID().toString();
        requestStatusStore.put(requestId, new TaskStatusResponse(
                requestId,
                "",
                "ORDER_SUBMITTED",
                "Order is being processed"));

        String orderId = createOrder(request, totalPrice);
        requestStatusStore.put(requestId, new TaskStatusResponse(
                requestId,
                orderId,
                "ORDER_CREATED",
                "Order created successfully"));

        String paymentStatus = processPayment(orderId, totalPrice);
        if ("FAILED".equals(paymentStatus)) {
            updateOrderStatus(orderId, "PAYMENT_FAILED");
            requestStatusStore.put(requestId, new TaskStatusResponse(
                    requestId,
                    orderId,
                    "PAYMENT_FAILED",
                    "Payment failed"));
            return ResponseEntity.status(HttpStatus.ACCEPTED)
                    .body(new TaskOrderResponse(requestId, "PAYMENT_FAILED", "Payment failed"));
        }

        updateOrderStatus(orderId, "PAID");
        requestStatusStore.put(requestId, new TaskStatusResponse(
                requestId,
                orderId,
                "PAID",
                "Order paid successfully"));

        TaskOrderResponse response = new TaskOrderResponse(
                requestId,
                "ORDER_SUBMITTED",
                "Order is being processed");

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @GetMapping("/status/{requestId}")
    public ResponseEntity<TaskStatusResponse> getStatus(@PathVariable String requestId) {
        TaskStatusResponse status = requestStatusStore.get(requestId);
        if (status == null) {
            throw new RequestNotFoundException("Request not found");
        }
        return ResponseEntity.ok(status);
    }

    private String createOrder(TaskOrderRequest request, BigDecimal totalPrice) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("items", mapItems(request));
        payload.put("total", totalPrice);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                orderServiceBaseUrl + "/orders",
                HttpMethod.POST,
                new HttpEntity<>(payload),
                new ParameterizedTypeReference<>() {
                });

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new IllegalArgumentException("Invalid input data");
        }

        Object orderIdObj = response.getBody().get("orderId");
        if (orderIdObj == null) {
            throw new IllegalArgumentException("Invalid input data");
        }

        return String.valueOf(orderIdObj);
    }

    private String processPayment(String orderId, BigDecimal totalPrice) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("orderId", orderId);
        payload.put("amount", totalPrice);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                paymentServiceBaseUrl + "/payments",
                HttpMethod.POST,
                new HttpEntity<>(payload),
                new ParameterizedTypeReference<>() {
                });

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new IllegalArgumentException("Invalid input data");
        }

        Object statusObj = response.getBody().get("status");
        if (statusObj == null) {
            throw new IllegalArgumentException("Invalid input data");
        }

        return String.valueOf(statusObj);
    }

    private void updateOrderStatus(String orderId, String status) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("status", status);
        restTemplate.exchange(
                orderServiceBaseUrl + "/orders/" + orderId + "/status",
                HttpMethod.PUT,
                new HttpEntity<>(payload),
                new ParameterizedTypeReference<Map<String, Object>>() {
                });
    }

    private List<Map<String, Object>> mapItems(TaskOrderRequest request) {
        return request.getItems().stream().map(item -> {
            Map<String, Object> mapped = new HashMap<>();
            mapped.put("id", item.getId());
            mapped.put("name", item.getName());
            mapped.put("price", item.getPrice());
            return mapped;
        }).toList();
    }
}