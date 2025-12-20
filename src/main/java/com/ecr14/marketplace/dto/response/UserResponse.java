package com.ecr14.marketplace.dto.response;

import com.ecr14.marketplace.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private String id;
    private String phone;
    private String role;
    private String name;
    private String brandId;

    public static UserResponse fromEntity(User user) {
        return new UserResponse(
                user.getId(),
                user.getPhone(),
                user.getRole().name().toLowerCase(),
                user.getName(),
                user.getBrandId()
        );
    }
}
