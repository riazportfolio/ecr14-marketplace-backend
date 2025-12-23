package com.ecr14.marketplace.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {

    private String id;
    private String brandId;
    private String brandName;
    private List<CartItemResponse> items;
    private Double totalAmount;
    private Integer itemCount;
}
