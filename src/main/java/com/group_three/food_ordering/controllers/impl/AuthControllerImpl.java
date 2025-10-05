package com.group_three.food_ordering.controllers.impl;

import com.group_three.food_ordering.controllers.AuthController;
import com.group_three.food_ordering.dto.request.UserRequestDto;
import com.group_three.food_ordering.dto.request.LoginRequest;
import com.group_three.food_ordering.dto.request.RefreshTokenRequest;
import com.group_three.food_ordering.dto.response.AuthResponse;
import com.group_three.food_ordering.dto.response.UserResponseDto;
import com.group_three.food_ordering.dto.response.LoginResponse;
import com.group_three.food_ordering.services.AuthService;
import com.group_three.food_ordering.services.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class AuthControllerImpl implements AuthController {

    private final AuthService authService;
    private final UserService userService;

    @Override
    public ResponseEntity<LoginResponse> login(LoginRequest request) {
        log.debug("[AuthController] Login request received");
        return ResponseEntity.ok(authService.login(request));
    }

    @Override
    public ResponseEntity<UserResponseDto> register(UserRequestDto dto) {
        log.debug("[AuthController] Register request received");
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(dto));
    }

    @Override
    public ResponseEntity<AuthResponse> refreshToken(RefreshTokenRequest request) {
        log.debug("[AuthController] Refresh token request received");
        AuthResponse response = authService.refreshAccessToken(request);
        return ResponseEntity.ok(response);
    }

    @Override
    public ResponseEntity<Void> logout(RefreshTokenRequest request) {
        log.debug("[AuthController] Logout request received");
        if (request != null && request.refreshToken() != null) {
            authService.logout(request.refreshToken());
        }
        return ResponseEntity.ok().build();
    }
}
