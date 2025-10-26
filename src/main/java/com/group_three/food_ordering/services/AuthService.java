package com.group_three.food_ordering.services;

import com.group_three.food_ordering.dto.request.LoginRequest;
import com.group_three.food_ordering.dto.request.RefreshTokenRequest;
import com.group_three.food_ordering.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse login(LoginRequest loginRequest);

    void logout(String refreshToken);

    AuthResponse refreshAccessToken(RefreshTokenRequest request);

}