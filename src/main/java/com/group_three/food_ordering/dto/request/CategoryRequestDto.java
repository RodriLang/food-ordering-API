package com.group_three.food_ordering.dto.request;

import com.group_three.food_ordering.utils.OnCreate;
import com.group_three.food_ordering.utils.OnUpdate;
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
public class CategoryRequestDto {

    @NotBlank(message = "category name is required", groups = OnCreate.class)
    @Size(max = 50, message = "Tag name must be 50 characters or less", groups = {OnCreate.class, OnUpdate.class})
    private String name;

    private Long parentCategoryId;

}
