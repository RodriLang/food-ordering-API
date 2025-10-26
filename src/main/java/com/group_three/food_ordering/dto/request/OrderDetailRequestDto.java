package com.group_three.food_ordering.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailRequestDto {

    @NotBlank(message = "Product ID is required")
    private String productName;

    @Size(max = 255, message = "Special instructions must be 255 characters or less")
    private String specialInstructions;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

}