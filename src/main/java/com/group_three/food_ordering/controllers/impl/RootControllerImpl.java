package com.group_three.food_ordering.controllers.impl;

import com.group_three.food_ordering.controllers.RootController;
import com.group_three.food_ordering.dto.request.RootUserRequestDto;
import com.group_three.food_ordering.dto.response.FoodVenuePublicResponseDto;
import com.group_three.food_ordering.dto.response.UserResponseDto;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.UUID;

public class RootControllerImpl implements RootController {

    @Override
    public ResponseEntity<List<FoodVenuePublicResponseDto>> getAllRootUsers() {
        return null;
    }

    @Override
    public ResponseEntity<UserResponseDto> registerRootUser(RootUserRequestDto dto) {
        return null;
    }

    @Override
    public ResponseEntity<UserResponseDto> selectContext(UUID foodVenueId) {
        return null;
    }
}
