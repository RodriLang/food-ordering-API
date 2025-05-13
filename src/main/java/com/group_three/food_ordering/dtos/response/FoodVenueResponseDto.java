package com.group_three.food_ordering.dtos.response;

import com.group_three.food_ordering.models.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodVenueResponseDto {

    private UUID id;
    private String name;
    private Address address;
    private String email;
    private String phone;
    private String imageUrl;
}
