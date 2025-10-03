package com.group_three.food_ordering.dto.response;

import com.group_three.food_ordering.models.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodVenuePublicResponseDto {

    private String id;
    private String name;
    private String phone;
    private String imageUrl;
    private Address address;
}
