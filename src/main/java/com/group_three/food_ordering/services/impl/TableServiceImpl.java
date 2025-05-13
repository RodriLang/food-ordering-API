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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class TableServiceImpl implements ITableService {

    private final ITableRepository tableRepository;
    private final TableMapper tableMapper;

    @Override
    public TableResponseDto create(TableCreateDto tableCreateDto) {
        // Mapeamos el DTO a la entidad Table
        Table table = tableMapper.toEntity(tableCreateDto);

        // Asignamos el foodVenueId si no se ha hecho en el mapper
        if (tableCreateDto.getFoodVenueId() != null) {
            FoodVenue foodVenue = new FoodVenue();
            foodVenue.setId(tableCreateDto.getFoodVenueId());
            table.setFoodVenue(foodVenue);
        }

        // Asignamos el valor por defecto de status si no está presente
        if (table.getStatus() == null) {
            table.setStatus(TableStatus.AVAILABLE);
        }

        // Guardamos la entidad en el repositorio
        Table savedTable = tableRepository.save(table);

        // Mapeamos la entidad guardada a un DTO para devolverlo
        return tableMapper.toDTO(savedTable);
    }

    @Override
    public List<TableResponseDto> getAll() {
        return tableRepository.findAll().stream()
                .map(tableMapper::toDTO)
                .toList();
    }

    @Override
    public TableResponseDto getById(Long id) {
        Table table = tableRepository.findById(id)
                .orElseThrow(TableNotFoundException::new);
        return tableMapper.toDTO(table);
    }

    @Override
    public TableResponseDto update(TableUpdateDto tableUpdateDto) {
        Table table = new Table();
        tableRepository.save(table);
        return new TableResponseDto();
    }

    @Override
    public void delete(Long id) {
        tableRepository.deleteById(id);
    }
}
