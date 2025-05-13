package com.group_three.food_ordering.services.interfaces;

import com.group_three.food_ordering.dtos.response.FoodVenueResponseDto;
import com.group_three.food_ordering.dtos.update.FoodVenueUpdateDto;

public interface IMyFoodVenueService {

    FoodVenueResponseDto get();
    FoodVenueResponseDto update(FoodVenueUpdateDto foodVenueUpdateDto);
}
