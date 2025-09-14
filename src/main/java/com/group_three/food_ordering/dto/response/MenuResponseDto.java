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
public class MenuResponseDto {
    private String categoryName;
    private List<ProductResponseDto> products;

}
