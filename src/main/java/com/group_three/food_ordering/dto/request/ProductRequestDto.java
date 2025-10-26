package com.group_three.food_ordering.dto.request;

import com.group_three.food_ordering.utils.OnCreate;
import com.group_three.food_ordering.utils.OnUpdate;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ProductRequestDto {

    @NotNull(message = "name of product is required", groups = {OnCreate.class})
    @Size(min = 2, max = 100, groups = {OnCreate.class, OnUpdate.class})
    private String name;

    @Size(max = 255, message = "description of product must be 255 characters or less", groups = {OnCreate.class, OnUpdate.class})
    private String description;

    @NotNull(message = "price is required", groups = {OnCreate.class})
    @DecimalMin(value = "0.0", message = "price must be greater than or equal to 0", groups = {OnCreate.class, OnUpdate.class})
    private BigDecimal price;

    @Min(value = 0, message = "stock must be 0 or more", groups = {OnCreate.class, OnUpdate.class})
    private Integer stock;

    @Size(max = 255, groups = {OnCreate.class, OnUpdate.class})
    private String imageUrl;

    private UUID categoryId;

    private List<String> tags;

}
