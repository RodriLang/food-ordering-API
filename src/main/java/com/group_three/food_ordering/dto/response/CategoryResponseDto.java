package com.group_three.food_ordering.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryResponseDto {

    private Integer id;
    private String name;
    private List<CategoryResponseDto> childrenCategories;
}
