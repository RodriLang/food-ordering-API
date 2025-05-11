package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.dtos.FoodVenueCreateDto;
import com.group_three.food_ordering.dtos.FoodVenueResponseDto;
import com.group_three.food_ordering.dtos.FoodVenueUpdateDto;
import com.group_three.food_ordering.services.interfaces.IFoodVenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/food-venues")
@RequiredArgsConstructor
public class FoodVenueController {

    private final IFoodVenueService foodVenueService;

    @PostMapping
    public ResponseEntity<FoodVenueResponseDto> create(@RequestBody FoodVenueCreateDto foodVenueCreateDto) {
        return ResponseEntity.ok(foodVenueService.create(foodVenueCreateDto));
    }

    @GetMapping
    public ResponseEntity<List<FoodVenueResponseDto>> getFoodVenues() {
        return ResponseEntity.ok(foodVenueService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodVenueResponseDto> getFoodVenueById(@PathVariable UUID id) {
        return ResponseEntity.ok(foodVenueService.getById(id));
    }

    @PutMapping
    public ResponseEntity<FoodVenueResponseDto> update(@RequestBody FoodVenueUpdateDto foodVenueUpdateDto) {
        return ResponseEntity.ok(foodVenueService.update(foodVenueUpdateDto));
    }

    @PatchMapping
    public ResponseEntity<FoodVenueResponseDto> patch(@RequestBody FoodVenueUpdateDto foodVenueUpdateDto) {
        return ResponseEntity.ok(foodVenueService.update(foodVenueUpdateDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFoodVenue(@PathVariable UUID id) {
        foodVenueService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
