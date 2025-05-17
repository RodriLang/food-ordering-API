package com.group_three.food_ordering.dtos.response;

import com.group_three.food_ordering.models.Address;
import com.group_three.food_ordering.models.Employee;
import com.group_three.food_ordering.models.Menu;
import com.group_three.food_ordering.models.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodVenueResponseDto {

    private UUID id;
    private String name;
    private String email;
    private String phone;
    private String imageUrl;
    private Address address;
}
