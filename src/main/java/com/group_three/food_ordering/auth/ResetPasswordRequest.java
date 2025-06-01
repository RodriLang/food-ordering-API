package com.group_three.food_ordering.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ResetPasswordRequest(

        @Email(message = "Invalid email format")
        String email,

        @NotBlank(message = "Recovery Token is required")
        String recoveryToken,

        @NotBlank(message = "New Password is required")
        @Size(min = 6, message = "Password must be at least 6 characters long")
        String newPassword) {

}
