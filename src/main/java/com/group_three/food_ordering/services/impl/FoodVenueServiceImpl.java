package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.context.TenantContext;
import com.group_three.food_ordering.dto.request.FoodVenueRequestDto;
import com.group_three.food_ordering.dto.response.FoodVenueAdminResponseDto;
import com.group_three.food_ordering.dto.response.FoodVenuePublicResponseDto;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.mappers.FoodVenueMapper;
import com.group_three.food_ordering.mappers.VenueStyleMapper;
import com.group_three.food_ordering.models.FoodVenue;
import com.group_three.food_ordering.models.VenueStyle;
import com.group_three.food_ordering.repositories.FoodVenueRepository;
import com.group_three.food_ordering.services.FoodVenueService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.group_three.food_ordering.utils.EntityName.FOOD_VENUE;

@Slf4j
@Service
@RequiredArgsConstructor
public class FoodVenueServiceImpl implements FoodVenueService {

    private final FoodVenueRepository foodVenueRepository;
    private final FoodVenueMapper foodVenueMapper;
    private final TenantContext tenantContext;
    private final VenueStyleMapper venueStyleMapper;

    @Override
    public FoodVenueAdminResponseDto create(FoodVenueRequestDto foodVenueRequestDto) {
        log.debug("[FoodVenueService] Creating new foodVenue");
        FoodVenue foodVenue = foodVenueMapper.toEntity(foodVenueRequestDto);
        foodVenue.setPublicId(UUID.randomUUID());
        log.debug("[FoodVenueRepository] Calling save to create new food venue");
        return foodVenueMapper.toAdminDto(foodVenueRepository.save(foodVenue));
    }

    @Override
    public Page<FoodVenueAdminResponseDto> getAllAdmin(Pageable pageable) {
        log.debug("[FoodVenueRepository] Calling findAll for all food venues (Admin)");
        return foodVenueRepository.findAllByDeletedFalse(pageable)
                .map(foodVenueMapper::toAdminDto);
    }

    @Override
    public Page<FoodVenueAdminResponseDto> getDeleted(Pageable pageable) {
        log.debug("[FoodVenueRepository] Calling findAllDeleted for deleted food venues");
        return foodVenueRepository.findAllDeleted(pageable)
                .map(foodVenueMapper::toAdminDto);
    }

    @Override
    public Page<FoodVenuePublicResponseDto> getAllPublic(Pageable pageable) {
        log.debug("[FoodVenueRepository] Calling findAll for all food venues (Public)");
        return foodVenueRepository.findAllByDeletedFalse(pageable)
                .map(foodVenueMapper::toPublicDto);
    }

    @Override
    public FoodVenueAdminResponseDto getById(UUID id) {
        FoodVenue foodVenue = findEntityById(id);
        return foodVenueMapper.toAdminDto(foodVenue);
    }

    @Override
    public FoodVenue findEntityById(UUID id) {
        log.debug("[FoodVenueRepository] Calling findByPublicId for foodVenueId={}", id);
        return foodVenueRepository.findByPublicIdAndDeletedFalse(id)
                .orElseThrow(() -> new EntityNotFoundException(FOOD_VENUE, id.toString()));
    }

    @Override
    public FoodVenuePublicResponseDto getMyCurrentFoodVenue() {

        FoodVenue currentFoodVenue = tenantContext.requireFoodVenue();

        return foodVenueMapper.toPublicDto(currentFoodVenue);
    }

    @Override
    public FoodVenueAdminResponseDto getMyFoodVenue() {

        FoodVenue currentFoodVenue = tenantContext.requireFoodVenue();

        return foodVenueMapper.toAdminDto(currentFoodVenue);
    }

    @Override
    public FoodVenueAdminResponseDto update(UUID foodVenueId, FoodVenueRequestDto foodVenueRequestDto) {

        FoodVenue foodVenue = findEntityById(foodVenueId);
        foodVenueMapper.updateEntity(foodVenueRequestDto, foodVenue);

        log.debug("[FoodVenueRepository] Calling save to update food venue {}", foodVenueId);
        return foodVenueMapper.toAdminDto(foodVenueRepository.save(foodVenue));
    }

    @Override
    public FoodVenueAdminResponseDto updateMyCurrentFoodVenue(FoodVenueRequestDto dto) {

        FoodVenue entity = tenantContext.requireFoodVenue();

        foodVenueMapper.updateEntity(dto, entity);

        if (dto.getStyleRequestDto() != null) {
            VenueStyle style = entity.getVenueStyle();
            if (style == null) {
                style = new VenueStyle();
                style.setColorsComplete(false);
                entity.setVenueStyle(style);
            }
            venueStyleMapper.updateEntity(dto.getStyleRequestDto(), style);
        }

        entity = foodVenueRepository.save(entity);

        return foodVenueMapper.toAdminDto(entity);
    }


    @Override
    public void softDelete(UUID id) {
        FoodVenue foodVenue = findEntityById(id);
        foodVenue.setDeleted(Boolean.TRUE);
        log.debug("[FoodVenueRepository] Calling save to soft delete food venue {}", id);
        foodVenueRepository.save(foodVenue);
    }
}