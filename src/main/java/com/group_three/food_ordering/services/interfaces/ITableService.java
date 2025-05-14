package com.group_three.food_ordering.services.interfaces;

import com.group_three.food_ordering.dtos.create.TableCreateDto;
import com.group_three.food_ordering.dtos.response.TableResponseDto;
import com.group_three.food_ordering.dtos.update.TableUpdateDto;
import com.group_three.food_ordering.enums.TableStatus;

import java.util.List;

public interface ITableService {
    TableResponseDto create(TableCreateDto tableCreateDto);
    List<TableResponseDto> getAll();
    List<TableResponseDto> getAllByStatus(TableStatus status);
    TableResponseDto getById(Long id);
    TableResponseDto getByNumber(Integer number);
    TableResponseDto update(TableUpdateDto tableUpdateDto, Long id);
    void delete(Long id);
}
