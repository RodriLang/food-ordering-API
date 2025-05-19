package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.dtos.create.TableCreateDto;
import com.group_three.food_ordering.dtos.response.TableResponseDto;
import com.group_three.food_ordering.dtos.update.TableUpdateDto;
import com.group_three.food_ordering.enums.TableStatus;
import com.group_three.food_ordering.exceptions.TableNotFoundException;
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

import static com.group_three.food_ordering.services.impl.MyFoodVenueServiceImpl.HARDCODED_FOOD_VENUE_ID;

@Service
@RequiredArgsConstructor
public class TableServiceImpl implements ITableService {

    private final ITableRepository tableRepository;
    private final TableMapper tableMapper;

    @Override
    public TableResponseDto create(TableCreateDto tableCreateDto) {
        Table table = tableMapper.toEntity(tableCreateDto);

        FoodVenue foodVenue = new FoodVenue();
        foodVenue.setId(HARDCODED_FOOD_VENUE_ID);
        table.setFoodVenue(foodVenue);

        if (table.getStatus() == null) {
            table.setStatus(TableStatus.AVAILABLE);
        }

        Table savedTable = tableRepository.save(table);
        return tableMapper.toDTO(savedTable);
    }

    @Override
    public List<TableResponseDto> getAll() {
        return tableRepository.findByFoodVenueId(HARDCODED_FOOD_VENUE_ID).stream()
                .map(tableMapper::toDTO)
                .toList();
    }

    @Override
    public TableResponseDto getById(UUID id) {
        Table table = tableRepository.findByFoodVenueIdAndId(HARDCODED_FOOD_VENUE_ID, id)
                .orElseThrow(TableNotFoundException::new);
        return tableMapper.toDTO(table);
    }

    @Override
    public TableResponseDto getByNumber(Integer number) {
        Table table = tableRepository.findByFoodVenueIdAndNumber(HARDCODED_FOOD_VENUE_ID, number)
                .orElseThrow(TableNotFoundException::new);
        return tableMapper.toDTO(table);
    }

    @Override
    public List<TableResponseDto> getByFilters(TableStatus status, Integer capacity) {
        return tableRepository.findByFoodVenueIdAndFilters(HARDCODED_FOOD_VENUE_ID, status, capacity).stream()
                .map(tableMapper::toDTO)
                .toList();
    }

    @Override
    public TableResponseDto update(TableUpdateDto tableUpdateDto, UUID id) {
        Table table = tableRepository.findByFoodVenueIdAndId(HARDCODED_FOOD_VENUE_ID, id)
                .orElseThrow(TableNotFoundException::new);

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
        Table table = tableRepository.findByFoodVenueIdAndId(HARDCODED_FOOD_VENUE_ID, id)
                .orElseThrow(TableNotFoundException::new);

        table.getFoodVenue().getTables().remove(table);
    }
}
