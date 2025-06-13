package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.context.TenantContext;
import com.group_three.food_ordering.dtos.response.FoodVenueResponseDto;
import com.group_three.food_ordering.dtos.update.FoodVenueUpdateDto;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.mappers.FoodVenueMapper;
import com.group_three.food_ordering.models.Address;
import com.group_three.food_ordering.models.FoodVenue;
import com.group_three.food_ordering.repositories.IFoodVenueRepository;
import com.group_three.food_ordering.services.interfaces.IMyFoodVenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MyFoodVenueService implements IMyFoodVenueService {

    private final IFoodVenueRepository foodVenueRepository;
    private final FoodVenueMapper foodVenueMapper;
    private final TenantContext tenantContext;

    @Override
    public FoodVenueResponseDto get() {
        FoodVenue foodVenue = foodVenueRepository.findById(tenantContext.getCurrentFoodVenue().getId())
                .orElseThrow(() -> new EntityNotFoundException("Could not find food venue."));
        return foodVenueMapper.toDTO(foodVenue);
    }

    @Override
    public FoodVenueResponseDto update(FoodVenueUpdateDto foodVenueUpdateDto) {
        FoodVenue foodVenue = foodVenueRepository.findById(tenantContext.getCurrentFoodVenue().getId())
                .orElseThrow(() -> new EntityNotFoundException("Could not find food venue."));

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
