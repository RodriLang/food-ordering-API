package com.group_three.food_ordering.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CategoryCreateDto {

    @NotBlank(message = "category name is required")
    @Size(max = 50, message = "Tag name must be 50 characters or less")
    private String name;

    private Long parentCategoryId;

}
