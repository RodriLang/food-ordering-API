package com.group_three.food_ordering.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Address {

    @NotBlank(message = "Street is required")
    private String street;

    @NotNull(message = "Number is required")
    private String number;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "Province is required")
    private String province;

    @NotBlank(message = "Country is required")
    private String country;

    @NotBlank(message = "Postal code is required")
    private String postalCode;

}

