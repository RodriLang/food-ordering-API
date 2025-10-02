package com.group_three.food_ordering.dto.response;


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
public class ItemMenuResponseDto {

    private Long id;
    private String name;
    private String description;
    private String image;
    private BigDecimal price;
    private CategoryResponseDto category;
    private List<TagResponseDto> tags;
}
