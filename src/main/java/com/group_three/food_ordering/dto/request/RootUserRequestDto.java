package com.group_three.food_ordering.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RootUserRequestDto {

    @NotBlank(message = "User name is required")
    private String userEmail;

}

