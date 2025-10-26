package com.group_three.food_ordering.controllers.impl;

import com.group_three.food_ordering.controllers.PublicInfoController;
import com.group_three.food_ordering.dto.response.FoodVenuePublicResponseDto;
import com.group_three.food_ordering.dto.response.MenuResponseDto;
import com.group_three.food_ordering.dto.response.PageResponse;
import com.group_three.food_ordering.services.FoodVenueService;
import com.group_three.food_ordering.services.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class PublicInfoControllerImpl implements PublicInfoController {

    private final FoodVenueService foodVenueService;
    private final MenuService menuService;


    @Override
    public ResponseEntity<PageResponse<FoodVenuePublicResponseDto>> getPublicFoodVenues(Pageable pageable) {
        return ResponseEntity.ok(PageResponse.of(foodVenueService.getAllPublic(pageable)));
    }


    @Override
    public ResponseEntity<MenuResponseDto> getHierarchicalMenu(UUID foodVenueId, String category) {
        return ResponseEntity.ok(menuService.getHierarchicalMenuByFoodVenueId(foodVenueId, category));
    }
}
