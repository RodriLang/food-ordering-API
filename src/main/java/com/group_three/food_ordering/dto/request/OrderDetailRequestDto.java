package com.group_three.food_ordering.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailRequestDto {

    @NotNull(message = "Product ID is required")
    private UUID productId;

    @Size(max = 255, message = "Special instructions must be 255 characters or less")
    private String specialInstructions;

}