package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dtos.create.FoodVenueCreateDto;
import com.group_three.food_ordering.dtos.response.FoodVenueResponseDto;
import com.group_three.food_ordering.dtos.update.FoodVenueUpdateDto;
import com.group_three.food_ordering.services.interfaces.IFoodVenueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.VENUE_BASE)
@RequiredArgsConstructor
public class FoodVenueController {

    private final IFoodVenueService foodVenueService;

    @PostMapping
    public ResponseEntity<FoodVenueResponseDto> createFoodVenue(@RequestBody @Valid FoodVenueCreateDto foodVenueCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(foodVenueService.create(foodVenueCreateDto));
    }

    @GetMapping
    public ResponseEntity<List<FoodVenueResponseDto>> getFoodVenues() {
        return ResponseEntity.ok(foodVenueService.getAll());
    }

    @GetMapping("/{venueId}")
    public ResponseEntity<FoodVenueResponseDto> getFoodVenueById(@PathVariable UUID venueId) {
        return ResponseEntity.ok(foodVenueService.getById(venueId));
    }

    @PutMapping("/{venueId}")
    public ResponseEntity<FoodVenueResponseDto> update(@RequestBody @Valid FoodVenueUpdateDto foodVenueUpdateDto, @PathVariable UUID venueId) {
        return ResponseEntity.ok(foodVenueService.update(foodVenueUpdateDto));
    }

    @PatchMapping("/{venueId}")
    public ResponseEntity<FoodVenueResponseDto> patch(@RequestBody @Valid FoodVenueUpdateDto foodVenueUpdateDto, @PathVariable UUID venueId) {
        return ResponseEntity.ok(foodVenueService.update(foodVenueUpdateDto));
    }

    @DeleteMapping("/{venueId}")
    public ResponseEntity<Void> deleteFoodVenue(@PathVariable UUID venueId) {
        foodVenueService.delete(venueId);
        return ResponseEntity.noContent().build();
    }
}
