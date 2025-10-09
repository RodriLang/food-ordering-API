package com.group_three.food_ordering.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailRequestDto {

    @NotBlank(message = "Product ID is required")
    private String productName;

    @Size(max = 255, message = "Special instructions must be 255 characters or less")
    private String specialInstructions;

}