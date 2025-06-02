package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;

import com.group_three.food_ordering.dtos.create.LoginRequest;
import com.group_three.food_ordering.dtos.response.AuthResponse;
import com.group_three.food_ordering.repositories.IUserRepository;
import com.group_three.food_ordering.security.JwtUtil;
import com.group_three.food_ordering.services.impl.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.AUTH_URI)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login-employee")
    public ResponseEntity<AuthResponse> loginEmployee(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.loginEmployee(request));
    }

    @PostMapping("/login-client")
    public ResponseEntity<AuthResponse> loginClient(@RequestBody LoginRequest request, @RequestParam UUID tableId) {
        return ResponseEntity.ok(authService.loginClient(request, tableId));
    }
}