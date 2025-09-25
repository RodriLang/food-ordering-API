package com.group_three.food_ordering.dto.create;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TagCreateDto {
    @NotBlank(message = "Tag name is required")
    @Size(max = 50, message = "Tag name must be 50 characters or less")
    private String label;

}
