package com.group_three.food_ordering.services;

import com.group_three.food_ordering.dto.request.FoodVenueRequestDto;
import com.group_three.food_ordering.dto.response.FoodVenueAdminResponseDto;
import com.group_three.food_ordering.dto.response.FoodVenuePublicResponseDto;
import com.group_three.food_ordering.models.FoodVenue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface FoodVenueService {

    FoodVenueAdminResponseDto create(FoodVenueRequestDto foodVenueRequestDto);

    FoodVenue findEntityById(UUID id);

    Page<FoodVenueAdminResponseDto> getAllAdmin(Pageable pageable);

    Page<FoodVenueAdminResponseDto> getDeleted(Pageable pageable);

    Page<FoodVenuePublicResponseDto> getAllPublic(Pageable pageable);

    FoodVenueAdminResponseDto getById(UUID id);

    FoodVenuePublicResponseDto getMyCurrentFoodVenue();

    FoodVenueAdminResponseDto getMyFoodVenue();

    FoodVenueAdminResponseDto update(UUID foodVenueId , FoodVenueRequestDto foodVenueUpdateDto);

    FoodVenueAdminResponseDto updateMyCurrentFoodVenue(FoodVenueRequestDto foodVenueUpdateDto);

    void softDelete(UUID id);
}
