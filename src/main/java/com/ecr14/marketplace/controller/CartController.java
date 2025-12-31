package com.ecr14.marketplace.controller;

import com.ecr14.marketplace.dto.request.AddToCartRequest;
import com.ecr14.marketplace.dto.request.UpdateQuantityRequest;
import com.ecr14.marketplace.dto.response.CartItemResponse;
import com.ecr14.marketplace.dto.response.CartResponse;
import com.ecr14.marketplace.entity.Cart;
import com.ecr14.marketplace.entity.CartItem;
import com.ecr14.marketplace.service.CartService;
import com.ecr14.marketplace.util.SecurityUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:8081", "https://ecr14-marketplace.vercel.app"})
public class CartController {

    @Autowired
    private CartService cartService;

    @Autowired
    private SecurityUtils securityUtils;

    /**
     * Get current user's cart with items
     */
    @GetMapping
    public ResponseEntity<CartResponse> getCart() {
        String userId = securityUtils.getCurrentUserId();
        Cart cart = cartService.getCart(userId);
        List<CartItem> items = cartService.getCartItems(userId);
        Double totalAmount = cartService.calculateTotal(userId);

        List<CartItemResponse> itemResponses = items.stream()
                .map(CartItemResponse::fromEntity)
                .collect(Collectors.toList());

        CartResponse response = new CartResponse(
                cart.getId(),
                cart.getBrandId(),
                cart.getBrandName(),
                itemResponses,
                totalAmount,
                items.size()
        );

        return ResponseEntity.ok(response);
    }

    /**
     * Add item to cart
     */
    @PostMapping("/items")
    public ResponseEntity<Map<String, String>> addToCart(@Valid @RequestBody AddToCartRequest request) {
        String userId = securityUtils.getCurrentUserId();
        cartService.addToCart(userId, request.getProductId(), request.getQuantity());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Item added to cart successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Update item quantity
     */
    @PutMapping("/items/{productId}")
    public ResponseEntity<Map<String, String>> updateQuantity(
            @PathVariable String productId,
            @Valid @RequestBody UpdateQuantityRequest request) {
        String userId = securityUtils.getCurrentUserId();
        cartService.updateQuantity(userId, productId, request.getQuantity());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Quantity updated successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Remove item from cart
     */
    @DeleteMapping("/items/{productId}")
    public ResponseEntity<Map<String, String>> removeItem(@PathVariable String productId) {
        String userId = securityUtils.getCurrentUserId();
        cartService.removeFromCart(userId, productId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Item removed from cart successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Clear entire cart
     */
    @DeleteMapping
    public ResponseEntity<Map<String, String>> clearCart() {
        String userId = securityUtils.getCurrentUserId();
        cartService.clearCart(userId);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Cart cleared successfully");
        return ResponseEntity.ok(response);
    }

    /**
     * Get cart item count (for badge)
     */
    @GetMapping("/count")
    public ResponseEntity<Long> getCartItemCount() {
        String userId = securityUtils.getCurrentUserId();
        Long count = cartService.getCartItemCount(userId);
        return ResponseEntity.ok(count);
    }
}
