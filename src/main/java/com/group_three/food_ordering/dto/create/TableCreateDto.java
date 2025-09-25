package com.group_three.food_ordering.dto.create;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TableCreateDto {

    @NotNull(message = "Number is required")
    private Integer number;

    @NotNull(message = "Capacity is required")
    private Integer capacity;
}
