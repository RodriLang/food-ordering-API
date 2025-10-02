package com.group_three.food_ordering.controllers.impl;

import com.group_three.food_ordering.controllers.PublicInfoController;
import com.group_three.food_ordering.dto.response.FlatMenuResponseDto;
import com.group_three.food_ordering.dto.response.FoodVenuePublicResponseDto;
import com.group_three.food_ordering.dto.response.HierarchicalMenuResponseDto;
import com.group_three.food_ordering.services.FoodVenueService;
import com.group_three.food_ordering.services.MenuService;
import com.group_three.food_ordering.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PublicInfoControllerImpl implements PublicInfoController {

    private final FoodVenueService foodVenueService;
    private final UserService userService;
    private final MenuService menuService;


    @Override
    public ResponseEntity<Page<FoodVenuePublicResponseDto>> getPublicFoodVenues(Pageable pageable) {
        return ResponseEntity.ok(foodVenueService.getAllPublic(pageable));
    }

    @Override
    public ResponseEntity<FlatMenuResponseDto> getFlatMenu(UUID foodVenueId) {
        return ResponseEntity.ok(menuService.getFlatMenuByFoodVenueId(foodVenueId));
    }

    @Override
    public ResponseEntity<HierarchicalMenuResponseDto> getHierarchicalMenu(UUID foodVenueId) {
        return ResponseEntity.ok(menuService.getHierarchicalMenuByFoodVenueId(foodVenueId));
    }
}
