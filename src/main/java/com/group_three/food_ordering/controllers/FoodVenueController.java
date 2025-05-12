package com.group_three.food_ordering.controllers;

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
@RequestMapping("/api/v1/food-venues")
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

    @GetMapping("/{id}")
    public ResponseEntity<FoodVenueResponseDto> getFoodVenueById(@PathVariable UUID id) {
        return ResponseEntity.ok(foodVenueService.getById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FoodVenueResponseDto> update(@RequestBody @Valid FoodVenueUpdateDto foodVenueUpdateDto, @PathVariable UUID id) {
        return ResponseEntity.ok(foodVenueService.update(foodVenueUpdateDto));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<FoodVenueResponseDto> patch(@RequestBody @Valid FoodVenueUpdateDto foodVenueUpdateDto, @PathVariable UUID id) {
        return ResponseEntity.ok(foodVenueService.update(foodVenueUpdateDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFoodVenue(@PathVariable UUID id) {
        foodVenueService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
