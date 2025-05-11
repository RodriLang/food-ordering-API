package com.group_three.food_ordering.services.interfaces;

import com.group_three.food_ordering.dtos.create.TableCreateDto;
import com.group_three.food_ordering.dtos.response.TableResponseDto;
import com.group_three.food_ordering.dtos.update.TableUpdateDto;

import java.util.List;

public interface ITableService {
    TableResponseDto create(TableCreateDto tableCreateDto);
    List<TableResponseDto> getAll();
    TableResponseDto getById(Long id);
    TableResponseDto update(TableUpdateDto tableUpdateDto);
    void delete(Long id);
}
