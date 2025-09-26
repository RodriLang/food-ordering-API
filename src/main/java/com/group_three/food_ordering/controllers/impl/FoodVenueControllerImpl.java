package com.group_three.food_ordering.controllers.impl;

import com.group_three.food_ordering.controllers.FoodVenueController;
import com.group_three.food_ordering.dto.request.FoodVenueRequestDto;
import com.group_three.food_ordering.dto.response.FoodVenueAdminResponseDto;
import com.group_three.food_ordering.dto.response.FoodVenuePublicResponseDto;
import com.group_three.food_ordering.services.FoodVenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class FoodVenueControllerImpl implements FoodVenueController {

    private final FoodVenueService foodVenueService;

    @Override
    @PreAuthorize("hasRole('ROOT')")
    public ResponseEntity<FoodVenueAdminResponseDto> createFoodVenue(FoodVenueRequestDto foodVenueRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(foodVenueService.create(foodVenueRequestDto));
    }

    @Override
    @PreAuthorize("hasRole('ROOT')")
    public ResponseEntity<Page<FoodVenueAdminResponseDto>> getFoodVenues(Pageable pageable) {
        return ResponseEntity.ok(foodVenueService.getAllAdmin(pageable));
    }

    @Override
    @PreAuthorize("hasRole('ROOT')")
    public ResponseEntity<Page<FoodVenueAdminResponseDto>> getDeletedFoodVenues(Pageable pageable) {
        return ResponseEntity.ok(foodVenueService.getDeleted(pageable));
    }

    @Override
    @PreAuthorize("hasRole('ROOT')")
    public ResponseEntity<FoodVenueAdminResponseDto> getFoodVenueById(
            UUID id) {
        return ResponseEntity.ok(foodVenueService.getById(id));
    }

    @Override
    @PreAuthorize("hasRole('ROOT')")
    public ResponseEntity<FoodVenueAdminResponseDto> updateById(UUID id, FoodVenueRequestDto foodVenueRequestDto) {
        return ResponseEntity.ok(foodVenueService.update(id, foodVenueRequestDto));
    }

    @Override
    @PreAuthorize("hasRole('ROOT')")
    public ResponseEntity<Void> deleteFoodVenue(UUID id) {
        foodVenueService.softDelete(id);
        return ResponseEntity.noContent().build();
    }

    @Override
    @PreAuthorize("hasAnyRole('GUEST', 'CLIENT', 'STAFF', 'ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<FoodVenuePublicResponseDto> getMyCurrentFoodVenue() {
        return ResponseEntity.ok(foodVenueService.getMyCurrentFoodVenue());
    }

    @Override
    @PreAuthorize("hasAnyRole('ADMIN', 'SUPER_ADMIN')")
    public ResponseEntity<FoodVenuePublicResponseDto> updateMyCurrentFoodVenue(FoodVenueRequestDto foodVenueRequestDto) {
        return ResponseEntity.ok(foodVenueService.updateMyCurrentFoodVenue(foodVenueRequestDto));
    }
}
