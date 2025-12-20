package com.ecr14.marketplace.service;

import com.ecr14.marketplace.dto.request.LoginRequest;
import com.ecr14.marketplace.dto.request.RegisterRequest;
import com.ecr14.marketplace.dto.response.AuthResponse;
import com.ecr14.marketplace.dto.response.UserResponse;
import com.ecr14.marketplace.entity.User;
import com.ecr14.marketplace.entity.UserRole;
import com.ecr14.marketplace.exception.BadRequestException;
import com.ecr14.marketplace.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByPhone(request.getPhone()).orElse(null);

        if (user == null) {
            return AuthResponse.error("User not found");
        }

        // Check if password is required for this user role
        if (user.getRole() == UserRole.ADMIN || user.getRole() == UserRole.SUPERADMIN) {
            if (request.getPassword() == null || request.getPassword().isEmpty()) {
                return AuthResponse.error("Password is required");
            }

            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                return AuthResponse.error("Invalid credentials");
            }
        }

        String token = jwtService.generateToken(user);
        return AuthResponse.success(UserResponse.fromEntity(user), token);
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByPhone(request.getPhone())) {
            return AuthResponse.error("Phone number already registered");
        }

        User user = new User();
        user.setPhone(request.getPhone());
        user.setName(request.getName());
        user.setRole(UserRole.CUSTOMER);

        user = userRepository.save(user);

        String token = jwtService.generateToken(user);
        return AuthResponse.success(UserResponse.fromEntity(user), token);
    }

    public boolean checkPasswordRequired(String phone) {
        User user = userRepository.findByPhone(phone).orElse(null);
        if (user == null) {
            return false;
        }
        return user.getRole() == UserRole.ADMIN || user.getRole() == UserRole.SUPERADMIN;
    }
}
