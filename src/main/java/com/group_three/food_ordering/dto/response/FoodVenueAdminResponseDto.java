package com.group_three.food_ordering.dto.response;

import com.group_three.food_ordering.models.Address;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodVenueAdminResponseDto {

    private UUID id;
    private String name;
    private String email;
    private String phone;
    private String imageUrl;
    private Address address;
    private LocalDateTime creationDate;
    private LocalDateTime lastUpdateDate;
    private Integer numberOfEmployees;
    private Integer numberOfTables;

}
