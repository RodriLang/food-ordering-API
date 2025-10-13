package com.group_three.food_ordering.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FoodVenueAdminResponseDto {

    private UUID publicId;

    private String name;

    private String email;

    private String phone;

    private String imageUrl;

    private AddressResponseDto address;

    private Instant creationDate;

    private Instant lastUpdateDate;

    private Integer numberOfEmployees;

    private Integer numberOfTables;

}
