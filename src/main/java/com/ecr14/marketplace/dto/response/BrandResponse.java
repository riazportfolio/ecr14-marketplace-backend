package com.ecr14.marketplace.dto.response;

import com.ecr14.marketplace.entity.Brand;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BrandResponse {

    private String id;
    private String name;
    private String description;
    private String logo;
    private String phone;
    private String email;
    private List<String> categories;
    private String ownerId;

    public static BrandResponse fromEntity(Brand brand) {
        return new BrandResponse(
                brand.getId(),
                brand.getName(),
                brand.getDescription(),
                brand.getLogo(),
                brand.getPhone(),
                brand.getEmail(),
                brand.getCategories(),
                brand.getOwnerId()
        );
    }
}
