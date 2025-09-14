package com.group_three.food_ordering.dto.update;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductUpdateDto {
    @Size(min = 2, max = 100)
    private String name;

    @Size(max = 255 , message = "description of product must be 255 characters or less")
    private String description;

    @DecimalMin(value = "0.0", inclusive = true, message = "price must be greater than or equal to 0")
    private BigDecimal price;

    @Min(value = 0, message = "stock must be 0 or more")
    private Integer stock;

    @Size(max = 255)
    private String imageUrl;

    private Long categoryId;
    private List<Long> tagIds;

}
