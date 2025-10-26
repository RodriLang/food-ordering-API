package com.group_three.food_ordering.dto.request;

import com.group_three.food_ordering.utils.OnCreate;
import com.group_three.food_ordering.utils.OnUpdate;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressRequestDto {

    @NotNull(message = "Street is required", groups = {OnCreate.class})
    @Size(min = 3, max = 100, groups = {OnCreate.class, OnUpdate.class})
    private String street;

    @NotNull(message = "Number is required", groups = {OnCreate.class})
    @Size(min = 1, max = 10, groups = {OnCreate.class, OnUpdate.class})
    private String number;

    @NotNull(message = "City is required", groups = {OnCreate.class})
    @Size(min = 3, max = 50, groups = {OnCreate.class, OnUpdate.class})
    private String city;

    @NotNull(message = "Province is required", groups = {OnCreate.class})
    @Size(min = 3, max = 50, groups = {OnCreate.class, OnUpdate.class})
    private String province;

    @NotNull(message = "Postal code is required", groups = {OnCreate.class})
    @Size(min = 3, max = 20, groups = {OnCreate.class, OnUpdate.class})
    private String postalCode;

    @NotNull(message = "Country is required", groups = {OnCreate.class})
    @Size(min = 3, max = 50, groups = {OnCreate.class, OnUpdate.class})
    private String country;
}
