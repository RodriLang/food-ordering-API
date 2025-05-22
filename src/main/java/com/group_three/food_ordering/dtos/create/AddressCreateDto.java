package com.group_three.food_ordering.dtos.create;

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
public class AddressCreateDto {

    @NotNull(message = "Street is required")
    @Size(min = 3, max = 100)
    private String street;

    @NotNull(message = "Number is required")
    @Size(min = 1, max = 10)
    private String number;

    @NotNull(message = "City is required")
    @Size(min = 3, max = 50)
    private String city;

    @NotNull(message = "Province is required")
    @Size(min = 3, max = 50)
    private String province;

    @NotNull(message = "Postal code is required")
    @Size(min = 3, max = 20)
    private String postalCode;

    @NotNull(message = "Country is required")
    @Size(min = 3, max = 50)
    private String country;
}
