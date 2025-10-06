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
import org.springframework.stereotype.Service;

import java.util.List;
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
        return diningTableMapper.toDTO(savedDiningTable);
    }

    @Override
    public List<DiningTableResponseDto> getAll() {
        return diningTableRepository.findByFoodVenuePublicId(
                        tenantContext.getCurrentFoodVenue().getPublicId()).stream()
                .map(diningTableMapper::toDTO)
                .toList();
    }

    @Override
    public DiningTableResponseDto getById(UUID id) {
        DiningTable diningTable = this.getEntityById(id);
        return diningTableMapper.toDTO(diningTable);
    }

    @Override
    public DiningTable getEntityById(UUID tableId) {
        return diningTableRepository.findByPublicId(tableId)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME, tableId.toString()));
    }

    @Override
    public DiningTableResponseDto getByNumber(Integer number) {
        DiningTable diningTable = diningTableRepository.findByFoodVenuePublicIdAndNumber(
                        tenantContext.getCurrentFoodVenue().getPublicId(), number)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME));
        return diningTableMapper.toDTO(diningTable);
    }

    @Override
    public List<DiningTableResponseDto> getByFilters(DiningTableStatus status, Integer capacity) {
        return diningTableRepository.findByFoodVenuePublicIdAndFiltersAndDeletedFalse(
                        tenantContext.getCurrentFoodVenue().getPublicId(), status, capacity).stream()
                .map(diningTableMapper::toDTO)
                .toList();
    }

    @Override
    public DiningTableResponseDto update(DiningTableRequestDto diningTableRequestDto, UUID id) {
        DiningTable diningTable = this.getEntityById(id);
        diningTableMapper.updateEntity(diningTable, diningTableRequestDto);

        if (diningTableRequestDto.getStatus() == null) {
            diningTable.setStatus(DiningTableStatus.AVAILABLE);
        }

        DiningTable updatedDiningTable = diningTableRepository.save(diningTable);
        return diningTableMapper.toDTO(updatedDiningTable);
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
