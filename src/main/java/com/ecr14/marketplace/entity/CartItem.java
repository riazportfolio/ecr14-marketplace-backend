package com.ecr14.marketplace.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "cart_items",
        uniqueConstraints = @UniqueConstraint(name = "unique_cart_product", columnNames = {"cart_id", "product_id"}),
        indexes = {
                @Index(name = "idx_cart_id", columnList = "cart_id")
        })
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CartItem {

    @Id
    private String id;

    @Column(name = "cart_id", nullable = false, length = 36)
    private String cartId;

    @Column(name = "product_id", nullable = false, length = 36)
    private String productId;

    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;

    @Column(name = "product_price", nullable = false)
    private Double productPrice;

    @Column(name = "product_unit", nullable = false, length = 50)
    private String productUnit;

    @Column(name = "product_image", length = 500)
    private String productImage;

    @Column(nullable = false)
    private Integer quantity = 1;

    @Column(name = "added_at", nullable = false)
    private LocalDateTime addedAt;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = java.util.UUID.randomUUID().toString();
        }
        if (addedAt == null) {
            addedAt = LocalDateTime.now();
        }
    }
}
