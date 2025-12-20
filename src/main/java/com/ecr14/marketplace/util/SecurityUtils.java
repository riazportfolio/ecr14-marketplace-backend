package com.ecr14.marketplace.util;

import com.ecr14.marketplace.exception.UnauthorizedException;
import com.ecr14.marketplace.service.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

@Component
public class SecurityUtils {

    @Autowired
    private JwtService jwtService;

    public String getCurrentUserId() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
        String authHeader = request.getHeader("Authorization");

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new UnauthorizedException("No authentication token found");
        }

        String token = authHeader.substring(7);
        String userId = (String) jwtService.getClaimsFromToken(token).get("userId");

        if (userId == null) {
            throw new UnauthorizedException("Invalid token");
        }

        return userId;
    }
}
