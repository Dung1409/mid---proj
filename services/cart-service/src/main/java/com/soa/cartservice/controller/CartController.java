package com.soa.cartservice.controller;

import com.soa.cartservice.dto.AddCartItemRequest;
import com.soa.cartservice.dto.CartItemResponse;
import com.soa.cartservice.dto.GetCartResponse;
import com.soa.cartservice.dto.MessageResponse;
import com.soa.cartservice.dto.UpdateQuantityRequest;
import com.soa.cartservice.entity.CartItem;
import com.soa.cartservice.repository.CartItemRepository;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cart")
public class CartController {
    private final CartItemRepository cartItemRepository;

    public CartController(CartItemRepository cartItemRepository) {
        this.cartItemRepository = cartItemRepository;
    }

    @PostMapping("/items")
    public ResponseEntity<MessageResponse> addItem(@Valid @RequestBody AddCartItemRequest request) {
        cartItemRepository.save(new CartItem(
                request.getItemId(),
                request.getName(),
                request.getPrice(),
                request.getQuantity()));
        return ResponseEntity.ok(new MessageResponse("Item added"));
    }

    @GetMapping
    public ResponseEntity<GetCartResponse> getCart() {
        List<CartItemResponse> items = cartItemRepository.findAll().stream()
                .map(item -> new CartItemResponse(
                        item.getItemId(),
                        item.getName(),
                        item.getPrice(),
                        item.getQuantity()))
                .collect(Collectors.toList());
        BigDecimal total = items.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return ResponseEntity.ok(new GetCartResponse(items, total));
    }

    @PutMapping("/items/{itemId}")
    public ResponseEntity<MessageResponse> updateItemQuantity(
            @PathVariable Integer itemId,
            @Valid @RequestBody UpdateQuantityRequest request) {
        CartItem existing = cartItemRepository.findById(itemId).orElse(null);
        if (existing == null) {
            throw new IllegalArgumentException("Item not found in cart");
        }

        existing.setQuantity(request.getQuantity());
        cartItemRepository.save(existing);
        return ResponseEntity.ok(new MessageResponse("Item quantity updated"));
    }

    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<MessageResponse> removeItem(@PathVariable Integer itemId) {
        if (!cartItemRepository.existsById(itemId)) {
            throw new IllegalArgumentException("Item not found in cart");
        }
        cartItemRepository.deleteById(itemId);
        return ResponseEntity.ok(new MessageResponse("Item removed"));
    }

    @DeleteMapping("/clear")
    public ResponseEntity<MessageResponse> clearCart() {
        cartItemRepository.deleteAll();
        return ResponseEntity.ok(new MessageResponse("Cart cleared"));
    }
}