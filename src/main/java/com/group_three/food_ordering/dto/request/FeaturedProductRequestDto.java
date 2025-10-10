package com.group_three.food_ordering.dto.request;

import com.group_three.food_ordering.utils.OnCreate;
import com.group_three.food_ordering.utils.OnUpdate;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FeaturedProductRequestDto {

    @NotNull(message = "Product name is required", groups = OnCreate.class)
    private String productName;

    @NotNull(message = "Featured from ID is required", groups = {OnCreate.class, OnUpdate.class})
    private LocalDateTime featuredFrom = LocalDateTime.now();

    @NotNull(message = "Featured until ID is required", groups = {OnCreate.class, OnUpdate.class})
    private LocalDateTime featuredUntil;

    private Integer priority = 0;

}

