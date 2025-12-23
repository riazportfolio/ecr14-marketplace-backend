package com.ecr14.marketplace.service;

import com.ecr14.marketplace.entity.*;
import com.ecr14.marketplace.exception.EmptyCartException;
import com.ecr14.marketplace.exception.InvalidDeliveryDateException;
import com.ecr14.marketplace.exception.ResourceNotFoundException;
import com.ecr14.marketplace.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private UserRepository userRepository;

    /**
     * Create order from cart
     */
    @Transactional
    public Order createOrder(String userId, LocalDate deliveryDate) {
        // Get user
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Get cart
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        // Get cart items
        List<CartItem> cartItems = cartItemRepository.findByCartIdOrderByAddedAtAsc(cart.getId());

        if (cartItems.isEmpty()) {
            throw new EmptyCartException();
        }

        // Get brand to validate delivery date
        Brand brand = brandRepository.findById(cart.getBrandId())
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found"));

        // Validate delivery date
        validateDeliveryDate(brand, deliveryDate);

        // Calculate total amount
        double totalAmount = cartItems.stream()
                .mapToDouble(item -> item.getProductPrice() * item.getQuantity())
                .sum();

        // Create order
        Order order = new Order();
        order.setUserId(user.getId());
        order.setUserName(user.getName());
        order.setUserPhone(user.getPhone());
        order.setApartmentNumber(user.getApartmentNumber());
        order.setDeliveryDate(deliveryDate);
        order.setBrandId(cart.getBrandId());
        order.setBrandName(cart.getBrandName());
        order.setBrandPhone(brand.getPhone());
        order.setTotalAmount(totalAmount);
        order.setStatus(OrderStatus.PENDING);
        order.setWhatsappSent(false);
        order.setCreatedAt(LocalDateTime.now());

        order = orderRepository.save(order);

        // Create order items from cart items
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(order.getId());
            orderItem.setProductId(cartItem.getProductId());
            orderItem.setProductName(cartItem.getProductName());
            orderItem.setProductPrice(cartItem.getProductPrice());
            orderItem.setProductUnit(cartItem.getProductUnit());
            orderItem.setQuantity(cartItem.getQuantity());
            orderItem.setSubtotal(cartItem.getProductPrice() * cartItem.getQuantity());
            orderItemRepository.save(orderItem);
        }

        // Clear cart after order creation
        cartItemRepository.deleteByCartId(cart.getId());
        cart.setBrandId(null);
        cart.setBrandName(null);
        cartRepository.save(cart);

        return order;
    }

    /**
     * Validate delivery date against brand's minimum notice days
     */
    private void validateDeliveryDate(Brand brand, LocalDate deliveryDate) {
        LocalDate today = LocalDate.now();
        LocalDate earliestDate = today.plusDays(brand.getMinNoticeDays());

        if (deliveryDate.isBefore(earliestDate)) {
            throw new InvalidDeliveryDateException(deliveryDate, earliestDate, brand.getMinNoticeDays());
        }
    }

    /**
     * Get customer order history
     */
    public List<Order> getOrderHistory(String userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId);
    }

    /**
     * Get order details with items
     */
    public Order getOrderDetails(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));
    }

    /**
     * Get order items
     */
    public List<OrderItem> getOrderItems(String orderId) {
        return orderItemRepository.findByOrderIdOrderById(orderId);
    }

    /**
     * Get brand orders (for admin dashboard)
     */
    public List<Order> getBrandOrders(String brandId, OrderStatus status) {
        if (status == null) {
            return orderRepository.findByBrandIdOrderByCreatedAtDesc(brandId);
        }
        return orderRepository.findByBrandIdAndStatusOrderByCreatedAtDesc(brandId, status);
    }

    /**
     * Get all orders by status (for superadmin)
     */
    public List<Order> getOrdersByStatus(OrderStatus status) {
        if (status == null) {
            return orderRepository.findAll();
        }
        return orderRepository.findByStatusOrderByCreatedAtDesc(status);
    }

    /**
     * Update order status
     */
    @Transactional
    public Order updateOrderStatus(String orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());

        return orderRepository.save(order);
    }

    /**
     * Mark WhatsApp as sent
     */
    @Transactional
    public void markWhatsAppSent(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotFoundException("Order not found"));

        order.setWhatsappSent(true);
        orderRepository.save(order);
    }

    /**
     * Check if delivery date is valid for brand
     */
    public boolean isDeliveryDateValid(String brandId, LocalDate deliveryDate) {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found"));

        LocalDate today = LocalDate.now();
        LocalDate earliestDate = today.plusDays(brand.getMinNoticeDays());

        return !deliveryDate.isBefore(earliestDate);
    }

    /**
     * Get earliest available delivery date for brand
     */
    public LocalDate getEarliestDeliveryDate(String brandId) {
        Brand brand = brandRepository.findById(brandId)
                .orElseThrow(() -> new ResourceNotFoundException("Brand not found"));

        return LocalDate.now().plusDays(brand.getMinNoticeDays());
    }
}
