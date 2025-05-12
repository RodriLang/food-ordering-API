package com.group_three.food_ordering.dtos.create;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderRequestDto {

    @Size(max = 255, message = "Special requirements must be 255 characters or less")
    private String specialRequirements;

<<<<<<<< HEAD:src/main/java/com/group_three/food_ordering/dtos/OrderRequestDto.java
    @NotNull(message = "Client is required")
    private UUID clientId;

    @Valid
    @NotEmpty(message = "Order must have at least one detail")
    private List<OrderDetailRequestDto> orderDetails = new ArrayList<>();
========
    /*@NotNull(message = "Food venue is required")
    private FoodVenueRequestDto foodVenue;

    @NotNull(message = "Client is required")
    private ClientRequestDto client;*/
>>>>>>>> 51b6d069bf85c3e1a3fade8bf7a763a32e77820e:src/main/java/com/group_three/food_ordering/dtos/create/OrderCreateDto.java
}

