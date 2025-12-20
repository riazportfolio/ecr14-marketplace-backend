package com.ecr14.marketplace.dto.response;

import com.ecr14.marketplace.entity.Product;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private String id;
    private String name;
    private String description;
    private String category;
    private Double price;
    private String unit;
    private String image;
    private String offer;
    private String brandId;
    private String brandName;
    private String brandPhone;

    public static ProductResponse fromEntity(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getDescription(),
                product.getCategory(),
                product.getPrice(),
                product.getUnit(),
                product.getImage(),
                product.getOffer(),
                product.getBrandId(),
                product.getBrandName(),
                product.getBrandPhone()
        );
    }
}
