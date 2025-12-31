package com.ecr14.marketplace.controller;

import com.ecr14.marketplace.dto.request.CreateOrderRequest;
import com.ecr14.marketplace.dto.request.UpdateOrderStatusRequest;
import com.ecr14.marketplace.dto.response.OrderItemResponse;
import com.ecr14.marketplace.dto.response.OrderResponse;
import com.ecr14.marketplace.entity.Order;
import com.ecr14.marketplace.entity.OrderItem;
import com.ecr14.marketplace.entity.OrderStatus;
import com.ecr14.marketplace.entity.User;
import com.ecr14.marketplace.entity.UserRole;
import com.ecr14.marketplace.exception.UnauthorizedException;
import com.ecr14.marketplace.repository.UserRepository;
import com.ecr14.marketplace.service.OrderService;
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
@RequestMapping("/api/orders")
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:8081", "https://ecr14-marketplace.vercel.app"})
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private SecurityUtils securityUtils;

    @Autowired
    private UserRepository userRepository;

    /**
     * Create order from cart
     */
    @PostMapping
    public ResponseEntity<OrderResponse> createOrder(@Valid @RequestBody CreateOrderRequest request) {
        String userId = securityUtils.getCurrentUserId();
        Order order = orderService.createOrder(userId, request.getDeliveryDate());
        List<OrderItem> items = orderService.getOrderItems(order.getId());

        List<OrderItemResponse> itemResponses = items.stream()
                .map(OrderItemResponse::fromEntity)
                .collect(Collectors.toList());

        OrderResponse response = OrderResponse.fromEntity(order, itemResponses);
        return ResponseEntity.ok(response);
    }

    /**
     * Get orders (customer: own orders, admin: brand orders)
     */
    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrders(
            @RequestParam(required = false) String status) {
        String userId = securityUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        List<Order> orders;

        if (user.getRole() == UserRole.CUSTOMER) {
            // Customer gets own orders
            orders = orderService.getOrderHistory(userId);
        } else if (user.getRole() == UserRole.ADMIN) {
            // Admin gets brand orders
            OrderStatus statusEnum = status != null ? OrderStatus.valueOf(status) : null;
            orders = orderService.getBrandOrders(user.getBrandId(), statusEnum);
        } else if (user.getRole() == UserRole.SUPERADMIN) {
            // SuperAdmin gets all orders
            OrderStatus statusEnum = status != null ? OrderStatus.valueOf(status) : null;
            orders = orderService.getOrdersByStatus(statusEnum);
        } else {
            throw new UnauthorizedException("Not authorized to view orders");
        }

        List<OrderResponse> responses = orders.stream()
                .map(order -> {
                    List<OrderItem> items = orderService.getOrderItems(order.getId());
                    List<OrderItemResponse> itemResponses = items.stream()
                            .map(OrderItemResponse::fromEntity)
                            .collect(Collectors.toList());
                    return OrderResponse.fromEntity(order, itemResponses);
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(responses);
    }

    /**
     * Get order details by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrderDetails(@PathVariable String id) {
        String userId = securityUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        Order order = orderService.getOrderDetails(id);

        // Check authorization
        if (user.getRole() == UserRole.CUSTOMER && !order.getUserId().equals(userId)) {
            throw new UnauthorizedException("Not authorized to view this order");
        } else if (user.getRole() == UserRole.ADMIN && !order.getBrandId().equals(user.getBrandId())) {
            throw new UnauthorizedException("Not authorized to view this order");
        }

        List<OrderItem> items = orderService.getOrderItems(id);
        List<OrderItemResponse> itemResponses = items.stream()
                .map(OrderItemResponse::fromEntity)
                .collect(Collectors.toList());

        OrderResponse response = OrderResponse.fromEntity(order, itemResponses);
        return ResponseEntity.ok(response);
    }

    /**
     * Update order status (Admin/SuperAdmin only)
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<OrderResponse> updateOrderStatus(
            @PathVariable String id,
            @Valid @RequestBody UpdateOrderStatusRequest request) {
        String userId = securityUtils.getCurrentUserId();
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UnauthorizedException("User not found"));

        // Only Admin and SuperAdmin can update status
        if (user.getRole() != UserRole.ADMIN && user.getRole() != UserRole.SUPERADMIN) {
            throw new UnauthorizedException("Not authorized to update order status");
        }

        Order order = orderService.getOrderDetails(id);

        // Admin can only update orders for their brand
        if (user.getRole() == UserRole.ADMIN && !order.getBrandId().equals(user.getBrandId())) {
            throw new UnauthorizedException("Not authorized to update this order");
        }

        Order updatedOrder = orderService.updateOrderStatus(id, request.getStatus());
        List<OrderItem> items = orderService.getOrderItems(id);
        List<OrderItemResponse> itemResponses = items.stream()
                .map(OrderItemResponse::fromEntity)
                .collect(Collectors.toList());

        OrderResponse response = OrderResponse.fromEntity(updatedOrder, itemResponses);
        return ResponseEntity.ok(response);
    }

    /**
     * Validate delivery date for current cart's brand
     */
    @GetMapping("/validate-date")
    public ResponseEntity<Map<String, Object>> validateDeliveryDate(
            @RequestParam String brandId,
            @RequestParam String date) {
        try {
            java.time.LocalDate deliveryDate = java.time.LocalDate.parse(date);
            boolean isValid = orderService.isDeliveryDateValid(brandId, deliveryDate);
            java.time.LocalDate earliestDate = orderService.getEarliestDeliveryDate(brandId);

            Map<String, Object> response = new HashMap<>();
            response.put("valid", isValid);
            response.put("earliestDate", earliestDate.toString());
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("valid", false);
            response.put("error", "Invalid date format");
            return ResponseEntity.badRequest().body(response);
        }
    }
}
