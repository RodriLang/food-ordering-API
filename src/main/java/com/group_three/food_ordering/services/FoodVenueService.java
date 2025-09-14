package com.group_three.food_ordering.services;

import com.group_three.food_ordering.dto.request.FoodVenueCreateDto;
import com.group_three.food_ordering.dto.response.FoodVenueResponseDto;
import com.group_three.food_ordering.dto.update.FoodVenueUpdateDto;

import java.util.List;
import java.util.UUID;

public interface FoodVenueService {

    // Only root can create a food venue
    FoodVenueResponseDto create(FoodVenueCreateDto foodVenueCreateDto);

    // Only root can view all food venues
    List<FoodVenueResponseDto> getAll();

    // Admin and root can view food venue by id
    FoodVenueResponseDto getById(UUID id);

    // Admin can update any food venue, root can update its food venue
    FoodVenueResponseDto update(FoodVenueUpdateDto foodVenueUpdateDto);

    // Admin can delete any food venue, root can delete its food venue
    void delete(UUID id);
}
