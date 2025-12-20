package com.ecr14.marketplace.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private UserResponse user;
    private String token;
    private String error;

    public static AuthResponse success(UserResponse user, String token) {
        return new AuthResponse(user, token, null);
    }

    public static AuthResponse error(String error) {
        return new AuthResponse(null, null, error);
    }
}
