package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;

import com.group_three.food_ordering.dtos.create.LoginRequest;
import com.group_three.food_ordering.dtos.response.AuthResponse;
import com.group_three.food_ordering.services.impl.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.AUTH_URI)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}