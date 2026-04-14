package com.soa.taskservice.controller;

import com.soa.taskservice.dto.TaskOrderRequest;
import com.soa.taskservice.dto.TaskOrderResponse;
import com.soa.taskservice.dto.TaskStatusResponse;
import com.soa.taskservice.exception.RequestNotFoundException;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.ArrayList;
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

    @Value("${cart.service.base-url}")
    private String cartServiceBaseUrl;

    @Value("${menu.service.base-url}")
    private String menuServiceBaseUrl;

    @PostMapping("/checkout")
    public ResponseEntity<TaskOrderResponse> submitOrder(@Valid @RequestBody TaskOrderRequest request) {
        List<Map<String, Object>> orderItems = fetchCheckoutItemsFromCart();

        // Validate items with menu service
        validateItemsWithMenu(orderItems);

        BigDecimal totalPrice = orderItems
                .stream()
                .map(item -> new BigDecimal(String.valueOf(item.get("price"))))
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

        String orderId = createOrder(orderItems, totalPrice);
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
        boolean cartCleared = clearCart();
        String paidMessage = cartCleared
                ? "Order paid successfully"
                : "Order paid successfully but cart clear failed";
        requestStatusStore.put(requestId, new TaskStatusResponse(
                requestId,
                orderId,
                "PAID",
                paidMessage));

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

    private void validateItemsWithMenu(List<Map<String, Object>> orderItems) {
        List<Integer> itemIds = new ArrayList<>();
        for (Map<String, Object> item : orderItems) {
            Object idObj = item.get("id");
            if (idObj != null) {
                itemIds.add(Integer.parseInt(String.valueOf(idObj)));
            }
        }

        if (itemIds.isEmpty()) {
            throw new IllegalArgumentException("Invalid input data");
        }

        Map<String, Object> payload = new HashMap<>();
        payload.put("itemIds", itemIds);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                menuServiceBaseUrl + "/menu/validate",
                HttpMethod.POST,
                new HttpEntity<>(payload),
                new ParameterizedTypeReference<>() {
                });

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new IllegalArgumentException("Invalid input data");
        }

        Object validObj = response.getBody().get("valid");
        if (!"true".equals(String.valueOf(validObj)) && !(boolean) validObj) {
            Object errorObj = response.getBody().get("error");
            throw new IllegalArgumentException(String.valueOf(errorObj));
        }
    }

    private String createOrder(List<Map<String, Object>> orderItems, BigDecimal totalPrice) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("items", orderItems);
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

    private List<Map<String, Object>> fetchCheckoutItemsFromCart() {
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                cartServiceBaseUrl + "/cart",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new IllegalArgumentException("Invalid input data");
        }

        Object itemsObj = response.getBody().get("items");
        if (!(itemsObj instanceof List<?> rawItems) || rawItems.isEmpty()) {
            throw new IllegalArgumentException("Invalid input data");
        }

        List<Map<String, Object>> mappedItems = new ArrayList<>();
        for (Object rawItem : rawItems) {
            if (!(rawItem instanceof Map<?, ?> item)) {
                throw new IllegalArgumentException("Invalid input data");
            }

            Object id = item.get("itemId");
            Object name = item.get("name");
            Object price = item.get("price");
            Object quantity = item.get("quantity");

            if (id == null || name == null || price == null || quantity == null) {
                throw new IllegalArgumentException("Invalid input data");
            }

            BigDecimal unitPrice = new BigDecimal(String.valueOf(price));
            int qty = Integer.parseInt(String.valueOf(quantity));

            Map<String, Object> mapped = new HashMap<>();
            mapped.put("id", id);
            mapped.put("name", String.valueOf(name));
            mapped.put("price", unitPrice.multiply(BigDecimal.valueOf(qty)));
            mappedItems.add(mapped);
        }

        if (mappedItems.isEmpty()) {
            throw new IllegalArgumentException("Invalid input data");
        }

        return mappedItems;
    }

    private boolean clearCart() {
        try {
            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    cartServiceBaseUrl + "/cart/clear",
                    HttpMethod.DELETE,
                    null,
                    new ParameterizedTypeReference<>() {
                    });
            return response.getStatusCode().is2xxSuccessful();
        } catch (RuntimeException ex) {
            return false;
        }
    }
}