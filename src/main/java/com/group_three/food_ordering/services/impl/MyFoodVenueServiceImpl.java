package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.dtos.response.FoodVenueResponseDto;
import com.group_three.food_ordering.dtos.update.FoodVenueUpdateDto;
import com.group_three.food_ordering.exceptions.FoodVenueNotFoundException;
import com.group_three.food_ordering.mappers.FoodVenueMapper;
import com.group_three.food_ordering.models.FoodVenue;
import com.group_three.food_ordering.repositories.IFoodVenueRepository;
import com.group_three.food_ordering.services.interfaces.IMyFoodVenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MyFoodVenueServiceImpl implements IMyFoodVenueService {

    private final IFoodVenueRepository foodVenueRepository;
    private final FoodVenueMapper foodVenueMapper;

    // Cambiar por el id del food venue que se quiere mostrar dinámicamente cuando se implemente la autenticación
    public static final UUID HARDCODED_FOOD_VENUE_ID = UUID.fromString("da83359f-3a91-4a36-ac89-cd07d9499958");

    @Override
    public FoodVenueResponseDto get() {
        FoodVenue foodVenue = foodVenueRepository.findById(HARDCODED_FOOD_VENUE_ID)
                .orElseThrow(FoodVenueNotFoundException::new);
        return foodVenueMapper.toDTO(foodVenue);
    }

    @Override
    public FoodVenueResponseDto update(FoodVenueUpdateDto foodVenueUpdateDto) {
        return null;
    }
}
