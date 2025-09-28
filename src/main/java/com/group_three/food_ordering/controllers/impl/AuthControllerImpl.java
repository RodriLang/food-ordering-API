package com.group_three.food_ordering.controllers.impl;

import com.group_three.food_ordering.controllers.AuthController;
import com.group_three.food_ordering.dto.create.UserCreateDto;
import com.group_three.food_ordering.dto.request.LoginRequest;
import com.group_three.food_ordering.dto.response.UserResponseDto;
import com.group_three.food_ordering.security.LoginResponse;
import com.group_three.food_ordering.services.AuthService;
import com.group_three.food_ordering.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthControllerImpl implements AuthController {

    private final AuthService authService;
    private final UserService userService;

    @Override
    public ResponseEntity<LoginResponse> login(LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @Override
    public ResponseEntity<UserResponseDto> register(UserCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(dto));
    }
}



