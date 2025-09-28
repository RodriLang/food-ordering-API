package com.group_three.food_ordering.dto.response;

import com.group_three.food_ordering.security.LoginResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AuthResponse implements LoginResponse {

    private String token;
}