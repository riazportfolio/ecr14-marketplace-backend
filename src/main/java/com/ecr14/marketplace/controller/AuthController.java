package com.ecr14.marketplace.controller;

import com.ecr14.marketplace.dto.request.LoginRequest;
import com.ecr14.marketplace.dto.request.RegisterRequest;
import com.ecr14.marketplace.dto.response.AuthResponse;
import com.ecr14.marketplace.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // Since we're using stateless JWT, logout is handled client-side by removing the token
        return ResponseEntity.ok().build();
    }

    @GetMapping("/check-password")
    public ResponseEntity<Map<String, Boolean>> checkPasswordRequired(@RequestParam String phone) {
        boolean requiresPassword = authService.checkPasswordRequired(phone);
        return ResponseEntity.ok(Map.of("requiresPassword", requiresPassword));
    }
}
