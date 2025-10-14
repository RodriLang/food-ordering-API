package com.group_three.food_ordering.controllers.impl;

import com.group_three.food_ordering.controllers.DiningTableController;
import com.group_three.food_ordering.dto.request.DiningTableRequestDto;
import com.group_three.food_ordering.dto.response.DiningTableResponseDto;
import com.group_three.food_ordering.dto.response.PageResponse;
import com.group_three.food_ordering.enums.DiningTableStatus;
import com.group_three.food_ordering.services.DiningTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DiningTableControllerImpl implements DiningTableController {

    private final DiningTableService diningTableService;

    @Override
    public ResponseEntity<DiningTableResponseDto> createTable(
            DiningTableRequestDto diningTableRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(diningTableService.create(diningTableRequestDto));
    }

    @Override
    public ResponseEntity<PageResponse<DiningTableResponseDto>> getTables(Pageable pageable) {
        return ResponseEntity.ok(PageResponse.of(diningTableService.getAll(pageable)));
    }

    @Override
    public ResponseEntity<DiningTableResponseDto> getTableById(
            UUID id) {
        return ResponseEntity.ok(diningTableService.getById(id));
    }

    @Override
    public ResponseEntity<DiningTableResponseDto> getTableByNumber(
            Integer number) {
        return ResponseEntity.ok(diningTableService.getByNumber(number));
    }

    @Override
    public ResponseEntity<PageResponse<DiningTableResponseDto>> getFilteredTables(
            DiningTableStatus status,
            Integer capacity,
            Pageable pageable) {
        return ResponseEntity.ok(PageResponse.of(diningTableService.getByFilters(status, capacity, pageable)));
    }

    @Override
    public ResponseEntity<Void> updateStatus(
            DiningTableStatus status,
            UUID id) {
        diningTableService.updateStatus(status, id);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<DiningTableResponseDto> update(
            DiningTableRequestDto diningTableRequestDto, UUID id) {
        return ResponseEntity.ok(diningTableService.update(diningTableRequestDto, id));
    }

    @Override
    public ResponseEntity<Void> delete(UUID id) {
        diningTableService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
