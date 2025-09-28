package com.group_three.food_ordering.controllers.impl;

import com.group_three.food_ordering.controllers.PublicInfoController;
import com.group_three.food_ordering.dto.create.UserCreateDto;
import com.group_three.food_ordering.dto.response.FoodVenuePublicResponseDto;
import com.group_three.food_ordering.dto.response.UserResponseDto;
import com.group_three.food_ordering.services.FoodVenueService;
import com.group_three.food_ordering.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class PublicInfoControllerImpl implements PublicInfoController {

    private final FoodVenueService foodVenueService;
    private final UserService userService;

    @Override
    public ResponseEntity<Page<FoodVenuePublicResponseDto>> getPublicFoodVenues(Pageable pageable) {
        return ResponseEntity.ok(foodVenueService.getAllPublic(pageable));
    }

    @Override
    public ResponseEntity<UserResponseDto> register(
            UserCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.create(dto));
    }

}
