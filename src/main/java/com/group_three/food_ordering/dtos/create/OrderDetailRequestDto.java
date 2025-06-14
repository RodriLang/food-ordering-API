package com.group_three.food_ordering.dtos.create;


import jakarta.validation.constraints.NotNull;
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

    @Size(max = 255, message = "Special instructions must be 255 characters or less")
    private String specialInstructions;

    @NotNull(message = "Product ID is required")
    private Long productId;

}