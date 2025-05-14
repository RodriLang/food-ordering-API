package com.group_three.food_ordering.dtos.update;

import jakarta.validation.constraints.PositiveOrZero;
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
    @Size(max = 100)
    private String name;

    @Size(max = 255)
    private String description;

    @PositiveOrZero
    private BigDecimal price;

    @PositiveOrZero
    private Integer stock;

    @Size(max = 255)
    private String imageUrl;

    private List<Long> tagIds;

}
