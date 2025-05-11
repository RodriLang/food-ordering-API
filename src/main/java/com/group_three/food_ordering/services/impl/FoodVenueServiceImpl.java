package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.dtos.FoodVenueCreateDto;
import com.group_three.food_ordering.dtos.FoodVenueResponseDto;
import com.group_three.food_ordering.dtos.FoodVenueUpdateDto;
import com.group_three.food_ordering.exceptions.FoodVenueNotFoundException;
import com.group_three.food_ordering.mappers.IFoodVenueMapper;
import com.group_three.food_ordering.models.FoodVenue;
import com.group_three.food_ordering.repositories.IFoodVenueRepository;
import com.group_three.food_ordering.services.interfaces.IFoodVenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FoodVenueServiceImpl implements IFoodVenueService {

    private final IFoodVenueRepository foodVenueRepository;
    private final IFoodVenueMapper foodVenueMapper;

    @Override
    public FoodVenueResponseDto create(FoodVenueCreateDto foodVenueCreateDto) {
        FoodVenue foodVenue = foodVenueMapper.toEntity(foodVenueCreateDto);

        return foodVenueMapper.toDTO(foodVenueRepository.save(foodVenue));
    }

    @Override
    public List<FoodVenueResponseDto> getAll() {
        return foodVenueRepository.findAll().stream()
                .map(foodVenueMapper::toDTO)
                .toList();
    }

    @Override
    public FoodVenueResponseDto getById(UUID id) {
        FoodVenue foodVenue = foodVenueRepository.findById(id)
                .orElseThrow(FoodVenueNotFoundException::new);
        return foodVenueMapper.toDTO(foodVenue);
    }

    @Override
    public FoodVenueResponseDto update(FoodVenueUpdateDto foodVenueUpdateDto) {
        FoodVenue foodVenue = new FoodVenue();
        foodVenueRepository.save(foodVenue);
        return new FoodVenueResponseDto();
    }

    @Override
    public void delete(UUID id) {
        foodVenueRepository.deleteById(id);
    }
}
