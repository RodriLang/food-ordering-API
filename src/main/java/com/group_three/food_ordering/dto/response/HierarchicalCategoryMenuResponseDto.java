package com.group_three.food_ordering.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class HierarchicalCategoryMenuResponseDto {

    private String category;

    private List<HierarchicalCategoryMenuResponseDto> subcategory;

    private List<ItemMenuResponseDto> products;

}
