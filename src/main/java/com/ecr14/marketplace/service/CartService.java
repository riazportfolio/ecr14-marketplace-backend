package com.ecr14.marketplace.service;

import com.ecr14.marketplace.entity.Cart;
import com.ecr14.marketplace.entity.CartItem;
import com.ecr14.marketplace.entity.Product;
import com.ecr14.marketplace.exception.BrandMismatchException;
import com.ecr14.marketplace.exception.ResourceNotFoundException;
import com.ecr14.marketplace.repository.CartItemRepository;
import com.ecr14.marketplace.repository.CartRepository;
import com.ecr14.marketplace.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    @Autowired
    private ProductRepository productRepository;

    /**
     * Get or create cart for a user
     */
    public Cart getOrCreateCart(String userId) {
        return cartRepository.findByUserId(userId)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUserId(userId);
                    return cartRepository.save(newCart);
                });
    }

    /**
     * Add item to cart or update quantity if already exists
     * Throws BrandMismatchException if product is from different brand
     */
    @Transactional
    public Cart addToCart(String userId, String productId, Integer quantity) {
        // Get product details
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found"));

        // Get or create cart
        Cart cart = getOrCreateCart(userId);

        // Check brand compatibility if cart has items
        if (cart.getBrandId() != null && !cart.getBrandId().equals(product.getBrandId())) {
            throw new BrandMismatchException(
                    cart.getBrandId(),
                    cart.getBrandName(),
                    product.getBrandId(),
                    product.getBrandName()
            );
        }

        // Set brand info if this is first item
        if (cart.getBrandId() == null) {
            cart.setBrandId(product.getBrandId());
            cart.setBrandName(product.getBrandName());
            cartRepository.save(cart);
        }

        // Check if item already exists
        CartItem existingItem = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElse(null);

        if (existingItem != null) {
            // Update quantity
            existingItem.setQuantity(existingItem.getQuantity() + quantity);
            cartItemRepository.save(existingItem);
        } else {
            // Create new cart item
            CartItem newItem = new CartItem();
            newItem.setCartId(cart.getId());
            newItem.setProductId(product.getId());
            newItem.setProductName(product.getName());
            newItem.setProductPrice(product.getPrice());
            newItem.setProductUnit(product.getUnit());
            newItem.setProductImage(product.getImage());
            newItem.setQuantity(quantity);
            newItem.setAddedAt(LocalDateTime.now());
            cartItemRepository.save(newItem);
        }

        return cart;
    }

    /**
     * Update item quantity in cart
     */
    @Transactional
    public void updateQuantity(String userId, String productId, Integer quantity) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found in cart"));

        if (quantity <= 0) {
            // Remove item if quantity is 0 or negative
            cartItemRepository.delete(item);

            // Clear brand info if cart is now empty
            if (cartItemRepository.countByCartId(cart.getId()) == 0) {
                cart.setBrandId(null);
                cart.setBrandName(null);
                cartRepository.save(cart);
            }
        } else {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }
    }

    /**
     * Remove item from cart
     */
    @Transactional
    public void removeFromCart(String userId, String productId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        CartItem item = cartItemRepository.findByCartIdAndProductId(cart.getId(), productId)
                .orElseThrow(() -> new ResourceNotFoundException("Item not found in cart"));

        cartItemRepository.delete(item);

        // Clear brand info if cart is now empty
        if (cartItemRepository.countByCartId(cart.getId()) == 0) {
            cart.setBrandId(null);
            cart.setBrandName(null);
            cartRepository.save(cart);
        }
    }

    /**
     * Clear all items from cart
     */
    @Transactional
    public void clearCart(String userId) {
        Cart cart = cartRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Cart not found"));

        cartItemRepository.deleteByCartId(cart.getId());

        cart.setBrandId(null);
        cart.setBrandName(null);
        cartRepository.save(cart);
    }

    /**
     * Get cart with all items
     */
    public Cart getCart(String userId) {
        return getOrCreateCart(userId);
    }

    /**
     * Get all items in cart
     */
    public List<CartItem> getCartItems(String userId) {
        Cart cart = getOrCreateCart(userId);
        return cartItemRepository.findByCartIdOrderByAddedAtAsc(cart.getId());
    }

    /**
     * Get total item count in cart
     */
    public Long getCartItemCount(String userId) {
        Cart cart = cartRepository.findByUserId(userId).orElse(null);
        if (cart == null) {
            return 0L;
        }
        return cartItemRepository.countByCartId(cart.getId());
    }

    /**
     * Calculate total amount for cart
     */
    public Double calculateTotal(String userId) {
        List<CartItem> items = getCartItems(userId);
        return items.stream()
                .mapToDouble(item -> item.getProductPrice() * item.getQuantity())
                .sum();
    }
}
