package com.group_three.food_ordering.services.interfaces;

import com.group_three.food_ordering.dtos.create.FoodVenueCreateDto;
import com.group_three.food_ordering.dtos.response.FoodVenueResponseDto;
import com.group_three.food_ordering.dtos.update.FoodVenueUpdateDto;

import java.util.List;
import java.util.UUID;

public interface IFoodVenueService {
    FoodVenueResponseDto create(FoodVenueCreateDto foodVenueCreateDto);
    List<FoodVenueResponseDto> getAll();
    FoodVenueResponseDto getById(UUID id);
    FoodVenueResponseDto update(FoodVenueUpdateDto foodVenueUpdateDto);
    void delete(UUID id);
}
