package com.group_three.food_ordering.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@Builder
@AllArgsConstructor
public class AuthResponse implements LoginResponse {

    private String accessToken;

    private String refreshToken;

    private Instant accessTokenExpiresAt;

}