package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.context.TenantContext;
import com.group_three.food_ordering.dtos.create.TableCreateDto;
import com.group_three.food_ordering.dtos.response.TableResponseDto;
import com.group_three.food_ordering.dtos.update.TableUpdateDto;
import com.group_three.food_ordering.enums.TableStatus;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.mappers.TableMapper;
import com.group_three.food_ordering.models.FoodVenue;
import com.group_three.food_ordering.models.Table;
import com.group_three.food_ordering.repositories.ITableRepository;
import com.group_three.food_ordering.services.interfaces.ITableService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TableService implements ITableService {

    private final ITableRepository tableRepository;
    private final TableMapper tableMapper;
    private final TenantContext tenantContext;

    @Override
    public TableResponseDto create(TableCreateDto tableCreateDto) {
        Table table = tableMapper.toEntity(tableCreateDto);

        FoodVenue foodVenue = new FoodVenue();
        foodVenue.setId(tenantContext.getCurrentFoodVenue().getId());
        table.setFoodVenue(foodVenue);

        Table savedTable = tableRepository.save(table);
        return tableMapper.toDTO(savedTable);
    }

    @Override
    public List<TableResponseDto> getAll() {
        return tableRepository.findByFoodVenueId(tenantContext.getCurrentFoodVenue().getId()).stream()
                .map(tableMapper::toDTO)
                .toList();
    }

    @Override
    public TableResponseDto getById(UUID id) {
        Table table = this.getEntityById(id);
        return tableMapper.toDTO(table);
    }

    @Override
    public Table getEntityById(UUID tableId) {
        return tableRepository.findByFoodVenueIdAndId(tenantContext.getCurrentFoodVenueId(), tableId)
                .orElseThrow(() -> new EntityNotFoundException("Table", tableId.toString()));
    }

    @Override
    public TableResponseDto getByNumber(Integer number) {
        Table table = tableRepository.findByFoodVenueIdAndNumber(tenantContext.getCurrentFoodVenue().getId(), number)
                .orElseThrow(() -> new EntityNotFoundException("Table not Found with number " + number));
        return tableMapper.toDTO(table);
    }

    @Override
    public List<TableResponseDto> getByFilters(TableStatus status, Integer capacity) {
        return tableRepository.findByFoodVenueIdAndFilters(tenantContext.getCurrentFoodVenue().getId(), status, capacity).stream()
                .map(tableMapper::toDTO)
                .toList();
    }

    @Override
    public TableResponseDto update(TableUpdateDto tableUpdateDto, UUID id) {
        Table table = this.getEntityById(id);

        table.setNumber(tableUpdateDto.getNumber());
        table.setCapacity(tableUpdateDto.getCapacity());

        if (tableUpdateDto.getStatus() == null) {
            table.setStatus(TableStatus.AVAILABLE);
        }

        Table updatedTable = tableRepository.save(table);

        return tableMapper.toDTO(updatedTable);
    }

    @Transactional
    @Override
    public void delete(UUID id) {
        Table table = this.getEntityById(id);

        table.getFoodVenue().getTables().remove(table);
    }

}
