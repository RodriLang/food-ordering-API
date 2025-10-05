package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.context.TenantContext;
import com.group_three.food_ordering.dto.request.FoodVenueRequestDto;
import com.group_three.food_ordering.dto.response.FoodVenueAdminResponseDto;
import com.group_three.food_ordering.dto.response.FoodVenuePublicResponseDto;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.mappers.FoodVenueMapper;
import com.group_three.food_ordering.models.FoodVenue;
import com.group_three.food_ordering.repositories.FoodVenueRepository;
import com.group_three.food_ordering.services.FoodVenueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FoodVenueServiceImpl implements FoodVenueService {

    private final FoodVenueRepository foodVenueRepository;
    private final FoodVenueMapper foodVenueMapper;
    private final TenantContext tenantContext;

    @Override
    public FoodVenueAdminResponseDto create(FoodVenueRequestDto foodVenueRequestDto) {
        log.debug("[AuthService] Creating new foodVenue");
        FoodVenue foodVenue = foodVenueMapper.toEntity(foodVenueRequestDto);
        foodVenue.setPublicId(UUID.randomUUID());
        return foodVenueMapper.toAdminDto(foodVenueRepository.save(foodVenue));
    }

    @Override
    public Page<FoodVenueAdminResponseDto> getAllAdmin(Pageable pageable) {
        return foodVenueRepository.findAll(pageable)
                .map(foodVenueMapper::toAdminDto);
    }

    @Override
    public Page<FoodVenueAdminResponseDto> getDeleted(Pageable pageable) {
        return foodVenueRepository.findAllDeleted(pageable)
                .map(foodVenueMapper::toAdminDto);
    }

    @Override
    public Page<FoodVenuePublicResponseDto> getAllPublic(Pageable pageable) {
        return foodVenueRepository.findAll(pageable)
                .map(foodVenueMapper::toPublicDto);
    }

    @Override
    public FoodVenueAdminResponseDto getById(UUID id) {
        FoodVenue foodVenue = findEntityById(id);
        return foodVenueMapper.toAdminDto(foodVenue);
    }

    @Override
    public FoodVenue findEntityById(UUID id) {
        return foodVenueRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Food Venue", id.toString()));
    }

    @Override
    public FoodVenuePublicResponseDto getMyCurrentFoodVenue() {

        FoodVenue currentFoodVenue = tenantContext.determineCurrentFoodVenue();

        return foodVenueMapper.toPublicDto(currentFoodVenue);
    }

    @Override
    public FoodVenueAdminResponseDto update(UUID foodVenueId, FoodVenueRequestDto foodVenueRequestDto) {

        FoodVenue foodVenue = findEntityById(foodVenueId);
        foodVenueMapper.updateEntity(foodVenueRequestDto, foodVenue);

        return foodVenueMapper.toAdminDto(foodVenueRepository.save(foodVenue));
    }

    @Override
    public FoodVenuePublicResponseDto updateMyCurrentFoodVenue(FoodVenueRequestDto foodVenueRequestDto) {

        FoodVenue foodVenue = tenantContext.getCurrentFoodVenue();
        foodVenueMapper.updateEntity(foodVenueRequestDto, foodVenue);

        return foodVenueMapper.toPublicDto(foodVenueRepository.save(foodVenue));
    }

    @Override
    public void softDelete(UUID id) {
        FoodVenue foodVenue = findEntityById(id);
        foodVenueRepository.delete(foodVenue);
    }
}
