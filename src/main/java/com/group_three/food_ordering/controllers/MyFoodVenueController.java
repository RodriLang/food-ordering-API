package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.dtos.response.FoodVenueResponseDto;
import com.group_three.food_ordering.dtos.update.FoodVenueUpdateDto;
import com.group_three.food_ordering.services.interfaces.IMyFoodVenueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/me/food-venue")
@RequiredArgsConstructor
public class MyFoodVenueController {

    private final IMyFoodVenueService myFoodVenueService;

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','ROOT')")
    @GetMapping
    public ResponseEntity<FoodVenueResponseDto> getMyFoodVenue() {
        return ResponseEntity.ok(myFoodVenueService.get());
    }

    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN','ROOT')")
    @PutMapping
    public ResponseEntity<FoodVenueResponseDto> updateMyFoodVenue(@RequestBody @Valid FoodVenueUpdateDto foodVenueUpdateDto) {
        return ResponseEntity.ok(myFoodVenueService.update(foodVenueUpdateDto));
    }
}
