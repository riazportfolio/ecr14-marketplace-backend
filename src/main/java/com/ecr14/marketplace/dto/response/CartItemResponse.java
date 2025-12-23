package com.ecr14.marketplace.dto.response;

import com.ecr14.marketplace.entity.CartItem;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {

    private String id;
    private String productId;
    private String productName;
    private Double productPrice;
    private String productUnit;
    private String productImage;
    private Integer quantity;
    private Double subtotal;

    public static CartItemResponse fromEntity(CartItem item) {
        return new CartItemResponse(
                item.getId(),
                item.getProductId(),
                item.getProductName(),
                item.getProductPrice(),
                item.getProductUnit(),
                item.getProductImage(),
                item.getQuantity(),
                item.getProductPrice() * item.getQuantity()
        );
    }
}
