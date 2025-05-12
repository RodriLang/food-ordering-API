package com.group_three.food_ordering.dtos.create;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductCreateDto {

    @NotNull(message = "name of product is required")
    private String name;
    @Size(max = 255 , message = "description of product must be 255 characters or less")
    private String description;

    @NotNull(message = "price is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "price must be greater than or equal to 0")
    private BigDecimal price;

    @Min(value = 0, message = "stock must be 0 or more")
    private Integer stock;
}
