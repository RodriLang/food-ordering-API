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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static com.group_three.food_ordering.utils.EntityName.DINING_TABLE;

@Slf4j
@Service
@RequiredArgsConstructor
public class DiningTableServiceImpl implements DiningTableService {

    private final DiningTableRepository diningTableRepository;
    private final DiningTableMapper diningTableMapper;
    private final TenantContext tenantContext;

    @Override
    public DiningTableResponseDto create(DiningTableRequestDto diningTableRequestDto) {
        DiningTable diningTable = diningTableMapper.toEntity(diningTableRequestDto);
        FoodVenue foodVenue = tenantContext.requireFoodVenue();
        diningTable.setFoodVenue(foodVenue);
        diningTable.setStatus(DiningTableStatus.AVAILABLE);
        diningTable.setPublicId(UUID.randomUUID());
        log.debug("[DiningTableRepository] Calling save to create new dining table for venue {}", foodVenue.getPublicId());
        DiningTable savedDiningTable = diningTableRepository.save(diningTable);
        return diningTableMapper.toDto(savedDiningTable);
    }

    @Override
    public Page<DiningTableResponseDto> getAll(Pageable pageable) {
        UUID foodVenueId = tenantContext.getFoodVenueId();
        log.debug("[DiningTableRepository] Calling findByFoodVenue_PublicIdAndDeletedFalse for venue {}", foodVenueId);
        return diningTableRepository.findByFoodVenue_PublicIdAndDeletedFalse(foodVenueId, pageable)
                .map(diningTableMapper::toDto);
    }

    @Override
    public DiningTableResponseDto getById(UUID id) {
        DiningTable diningTable = this.getEntityById(id);
        return diningTableMapper.toDto(diningTable);
    }

    @Override
    public DiningTable getEntityById(UUID tableId) {
        log.debug("[DiningTableRepository] Calling findByPublicId for tableId {}", tableId);
        return diningTableRepository.findByPublicIdAndDeletedFalse(tableId)
                .orElseThrow(() -> new EntityNotFoundException(DINING_TABLE, tableId.toString()));
    }

    @Override
    public DiningTableResponseDto getByNumber(Integer number) {
        UUID foodVenueId = tenantContext.getFoodVenueId();
        log.debug("[DiningTableRepository] Calling findByFoodVenuePublicIdAndNumber for venue {} and table number {}",
                foodVenueId, number);

        DiningTable diningTable = diningTableRepository.findByFoodVenuePublicIdAndNumber(foodVenueId, number)
                .orElseThrow(() -> new EntityNotFoundException(DINING_TABLE));
        return diningTableMapper.toDto(diningTable);
    }

    @Override
    public Page<DiningTableResponseDto> getByFilters(DiningTableStatus status, Integer capacity, Pageable pageable) {
        UUID foodVenueId = tenantContext.getFoodVenueId();
        log.debug("[DiningTableRepository] Calling findByFoodVenuePublicIdAndFiltersAndDeletedFalse for " +
                "venue {} with status {} and capacity {}", foodVenueId, status, capacity);

        return diningTableRepository.findByFoodVenuePublicIdAndFilters(
                foodVenueId, status, capacity, pageable).map(diningTableMapper::toDto);
    }

    @Override
    public DiningTableResponseDto update(DiningTableRequestDto diningTableRequestDto, UUID id) {
        DiningTable diningTable = this.getEntityById(id);
        diningTableMapper.updateEntity(diningTable, diningTableRequestDto);

        if (diningTableRequestDto.getStatus() == null) {
            diningTable.setStatus(DiningTableStatus.AVAILABLE);
        }

        log.debug("[DiningTableRepository] Calling save to update dining table {}", id);
        DiningTable updatedDiningTable = diningTableRepository.save(diningTable);
        return diningTableMapper.toDto(updatedDiningTable);
    }

    @Override
    public void updateStatus(DiningTableStatus status, UUID id) {
        DiningTable diningTable = this.getEntityById(id);

        if ((status != null)) {
            diningTable.setStatus(status);
        }
        log.debug("[DiningTableRepository] Calling save to update status to {} for table {}", status, id);
        diningTableRepository.save(diningTable);
    }

    @Override
    public void updateStatusByEntity(DiningTableStatus status, DiningTable diningTable) {

        if ((status != null)) {
            diningTable.setStatus(status);
        }
        log.debug("[DiningTableRepository] Calling save table number {} status {}",
                diningTable.getPublicId(), status);

        diningTableRepository.save(diningTable);
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        DiningTable diningTable = this.getEntityById(id);
        if (diningTable.getStatus() != DiningTableStatus.AVAILABLE && diningTable.getStatus() != DiningTableStatus.OUT_OF_SERVICE) {
            throw new IllegalStateException("Only tables with status AVAILABLE or OUT_OF_SERVICE can be deleted.");
        }
        diningTable.setDeleted(Boolean.TRUE);
        log.debug("[DiningTableRepository] Calling save to soft delete dining table {}", id);
        diningTableRepository.save(diningTable);
    }

    @Override
    public void save(DiningTable diningTable) {
        diningTableRepository.save(diningTable);
    }
}
