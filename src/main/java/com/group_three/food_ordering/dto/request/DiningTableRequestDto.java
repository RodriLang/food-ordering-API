package com.group_three.food_ordering.dto.request;

import com.group_three.food_ordering.enums.DiningTableStatus;
import com.group_three.food_ordering.utils.OnCreate;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiningTableRequestDto {

    @NotNull(message = "Number is required", groups = OnCreate.class)
    private Integer number;

    @NotNull(message = "Capacity is required", groups = OnCreate.class)
    private Integer capacity;

    private DiningTableStatus status;
}
