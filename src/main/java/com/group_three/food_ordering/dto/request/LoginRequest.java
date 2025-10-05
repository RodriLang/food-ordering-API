package com.group_three.food_ordering.dto.request;

import com.group_three.food_ordering.utils.OnCreate;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class LoginRequest {

    @NotBlank(message = "Email is required", groups = OnCreate.class)
    private String email;

    @NotBlank(message = "Password is required", groups = OnCreate.class)
    private String password;

}