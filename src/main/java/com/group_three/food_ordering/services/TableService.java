package com.group_three.food_ordering.services;

import com.group_three.food_ordering.dto.create.TableCreateDto;
import com.group_three.food_ordering.dto.response.TableResponseDto;
import com.group_three.food_ordering.dto.update.TableUpdateDto;
import com.group_three.food_ordering.enums.TableStatus;
import com.group_three.food_ordering.models.Table;

import java.util.List;
import java.util.UUID;

public interface TableService {

    TableResponseDto create(TableCreateDto tableCreateDto);
    List<TableResponseDto> getAll();
    TableResponseDto getById(UUID id);
    Table getEntityById(UUID id);
    TableResponseDto getByNumber(Integer number);
    TableResponseDto update(TableUpdateDto tableUpdateDto, UUID id);
    void delete(UUID id);

    List<TableResponseDto> getByFilters(TableStatus status, Integer capacity);
}
