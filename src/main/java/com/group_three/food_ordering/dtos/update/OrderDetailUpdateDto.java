package com.group_three.food_ordering.dtos.update;


import jakarta.validation.constraints.Min;
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
public class OrderDetailUpdateDto {

    private Integer quantity;

    @Size(max = 255, message = "Special instructions must be 255 characters or less")
    private String specialInstructions;

}

