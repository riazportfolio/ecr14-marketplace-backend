package com.ecr14.marketplace.dto.response;

import com.ecr14.marketplace.entity.OrderItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderItemResponse {

    private String id;
    private String productId;
    private String productName;
    private Double productPrice;
    private String productUnit;
    private Integer quantity;
    private Double subtotal;

    public static OrderItemResponse fromEntity(OrderItem item) {
        return new OrderItemResponse(
                item.getId(),
                item.getProductId(),
                item.getProductName(),
                item.getProductPrice(),
                item.getProductUnit(),
                item.getQuantity(),
                item.getSubtotal()
        );
    }
}
