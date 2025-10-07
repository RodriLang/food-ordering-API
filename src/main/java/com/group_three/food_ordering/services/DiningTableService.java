package com.group_three.food_ordering.services;

import com.group_three.food_ordering.dto.request.DiningTableRequestDto;
import com.group_three.food_ordering.dto.response.DiningTableResponseDto;
import com.group_three.food_ordering.enums.DiningTableStatus;
import com.group_three.food_ordering.models.DiningTable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

public interface DiningTableService {

    DiningTableResponseDto create(DiningTableRequestDto diningTableRequestDto);

    Page<DiningTableResponseDto> getAll(Pageable pageable);

    DiningTableResponseDto getById(UUID id);

    DiningTable getEntityById(UUID id);

    DiningTableResponseDto getByNumber(Integer number);

    DiningTableResponseDto update(DiningTableRequestDto diningTableRequestDto, UUID id);

    void updateStatus(DiningTableStatus status, UUID id);

    void delete(UUID id);

    Page<DiningTableResponseDto> getByFilters(DiningTableStatus status, Integer capacity, Pageable pageable);
}
