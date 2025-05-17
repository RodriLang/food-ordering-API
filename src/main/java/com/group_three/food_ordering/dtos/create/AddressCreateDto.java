package com.group_three.food_ordering.dtos.create;

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
public class AddressCreateDto {

    @NotBlank(message = "Street is required")
    @Size(min = 3, max = 100)
    private String street;

    @NotBlank(message = "Number is required")
    @Size(min = 1, max = 10)
    private String number;

    @NotBlank(message = "City is required")
    @Size(min = 3, max = 50)
    private String city;

    @NotBlank(message = "Province is required")
    @Size(min = 3, max = 50)
    private String province;

    @NotBlank(message = "Postal code is required")
    @Size(min = 3, max = 20)
    private String postalCode;

    @NotBlank(message = "Country is required")
    @Size(min = 3, max = 50)
    private String country;
}
