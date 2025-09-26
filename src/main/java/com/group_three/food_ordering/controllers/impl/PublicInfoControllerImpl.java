package com.group_three.food_ordering.controllers.impl;

import com.group_three.food_ordering.controllers.PublicInfoController;
import com.group_three.food_ordering.dto.response.FoodVenuePublicResponseDto;
import com.group_three.food_ordering.services.FoodVenueService;
import jakarta.annotation.security.PermitAll;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PublicInfoControllerImpl implements PublicInfoController {

    private final FoodVenueService foodVenueService;

    @Override
    @PermitAll
    public ResponseEntity<Page<FoodVenuePublicResponseDto>> getPublicFoodVenues(Pageable pageable) {
        return ResponseEntity.ok(foodVenueService.getAllPublic(pageable));
    }
}
