package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.dtos.response.FoodVenueResponseDto;
import com.group_three.food_ordering.dtos.update.FoodVenueUpdateDto;
import com.group_three.food_ordering.exceptions.FoodVenueNotFoundException;
import com.group_three.food_ordering.mappers.FoodVenueMapper;
import com.group_three.food_ordering.models.Address;
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
    public static final UUID HARDCODED_FOOD_VENUE_ID = UUID.fromString("67aff441-1545-4bf8-a003-bd300d96eeda");

    @Override
    public FoodVenueResponseDto get() {
        FoodVenue foodVenue = foodVenueRepository.findById(HARDCODED_FOOD_VENUE_ID)
                .orElseThrow(FoodVenueNotFoundException::new);
        return foodVenueMapper.toDTO(foodVenue);
    }

    @Override
    public FoodVenueResponseDto update(FoodVenueUpdateDto foodVenueUpdateDto) {
        FoodVenue foodVenue = foodVenueRepository.findById(HARDCODED_FOOD_VENUE_ID)
                .orElseThrow(FoodVenueNotFoundException::new);

        foodVenue.setName(foodVenueUpdateDto.getName());

        Address address = foodVenue.getAddress();
        address.setStreet(foodVenueUpdateDto.getAddress().getStreet());
        address.setNumber(foodVenueUpdateDto.getAddress().getNumber());
        address.setCity(foodVenueUpdateDto.getAddress().getCity());
        address.setProvince(foodVenueUpdateDto.getAddress().getProvince());
        address.setCountry(foodVenueUpdateDto.getAddress().getCountry());
        address.setPostalCode(foodVenueUpdateDto.getAddress().getPostalCode());

        foodVenue.setAddress(address);
        foodVenue.setEmail(foodVenueUpdateDto.getEmail());
        foodVenue.setPhone(foodVenueUpdateDto.getPhone());
        foodVenue.setImageUrl(foodVenueUpdateDto.getImageUrl());

        foodVenueRepository.save(foodVenue);
        return foodVenueMapper.toDTO(foodVenue);
    }
}
