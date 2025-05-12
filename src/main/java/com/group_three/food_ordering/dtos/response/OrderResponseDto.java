package com.group_three.food_ordering.dtos.response;

<<<<<<< HEAD:src/main/java/com/group_three/food_ordering/dtos/OrderResponseDto.java

=======
>>>>>>> 51b6d069bf85c3e1a3fade8bf7a763a32e77820e:src/main/java/com/group_three/food_ordering/dtos/response/OrderResponseDto.java
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderResponseDto {

    private String orderNumber;

    private String specialRequirements;

<<<<<<< HEAD:src/main/java/com/group_three/food_ordering/dtos/OrderResponseDto.java
    private String clientAlias;

    private List<OrderDetailResponseDto> orderDetails;
=======
    /*private FoodVenueRequestDto foodVenue;

    private ClientRequestDto client;*/
>>>>>>> 51b6d069bf85c3e1a3fade8bf7a763a32e77820e:src/main/java/com/group_three/food_ordering/dtos/response/OrderResponseDto.java
}
