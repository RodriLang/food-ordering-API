package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.context.TenantContext;
import com.group_three.food_ordering.dto.request.DiningTableRequestDto;
import com.group_three.food_ordering.dto.response.DiningTableResponseDto;
import com.group_three.food_ordering.enums.DiningTableStatus;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.mappers.DiningTableMapper;
import com.group_three.food_ordering.models.FoodVenue;
import com.group_three.food_ordering.models.DiningTable;
import com.group_three.food_ordering.repositories.DiningTableRepository;
import com.group_three.food_ordering.services.DiningTableService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DiningTableServiceImpl implements DiningTableService {

    private final DiningTableRepository diningTableRepository;
    private final DiningTableMapper diningTableMapper;
    private final TenantContext tenantContext;

    private static final String ENTITY_NAME = "Table";

    @Override
    public DiningTableResponseDto create(DiningTableRequestDto diningTableRequestDto) {
        DiningTable diningTable = diningTableMapper.toEntity(diningTableRequestDto);
        FoodVenue foodVenue = tenantContext.getCurrentFoodVenue();
        diningTable.setFoodVenue(foodVenue);
        diningTable.setStatus(DiningTableStatus.AVAILABLE);
        diningTable.setPublicId(UUID.randomUUID());
        DiningTable savedDiningTable = diningTableRepository.save(diningTable);
        return diningTableMapper.toDto(savedDiningTable);
    }

    @Override
    public Page<DiningTableResponseDto> getAll(Pageable pageable) {
        UUID foodVenueId = tenantContext.getCurrentFoodVenueId();
        return diningTableRepository.findByFoodVenuePublicId(foodVenueId, pageable)
                .map(diningTableMapper::toDto);
    }

    @Override
    public DiningTableResponseDto getById(UUID id) {
        DiningTable diningTable = this.getEntityById(id);
        return diningTableMapper.toDto(diningTable);
    }

    @Override
    public DiningTable getEntityById(UUID tableId) {
        return diningTableRepository.findByPublicId(tableId)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME, tableId.toString()));
    }

    @Override
    public DiningTableResponseDto getByNumber(Integer number) {
        UUID foodVenueId = tenantContext.getCurrentFoodVenueId();
        DiningTable diningTable = diningTableRepository.findByFoodVenuePublicIdAndNumber(foodVenueId, number)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME));
        return diningTableMapper.toDto(diningTable);
    }

    @Override
    public Page<DiningTableResponseDto> getByFilters(DiningTableStatus status, Integer capacity, Pageable pageable) {
        UUID foodVenueId = tenantContext.getCurrentFoodVenueId();
        return diningTableRepository.findByFoodVenuePublicIdAndFiltersAndDeletedFalse(
                foodVenueId, status, capacity, pageable).map(diningTableMapper::toDto);
    }

    @Override
    public DiningTableResponseDto update(DiningTableRequestDto diningTableRequestDto, UUID id) {
        DiningTable diningTable = this.getEntityById(id);
        diningTableMapper.updateEntity(diningTable, diningTableRequestDto);

        if (diningTableRequestDto.getStatus() == null) {
            diningTable.setStatus(DiningTableStatus.AVAILABLE);
        }

        DiningTable updatedDiningTable = diningTableRepository.save(diningTable);
        return diningTableMapper.toDto(updatedDiningTable);
    }

    @Override
    public void updateStatus(DiningTableStatus status, UUID id) {
        DiningTable diningTable = this.getEntityById(id);

        if ((status != null)) {
            diningTable.setStatus(status);
        }
        diningTableRepository.save(diningTable);
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        DiningTable diningTable = this.getEntityById(id);

        diningTable.getFoodVenue().getDiningTables().remove(diningTable);
    }
}
