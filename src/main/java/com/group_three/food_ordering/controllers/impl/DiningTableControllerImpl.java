package com.group_three.food_ordering.controllers.impl;

import com.group_three.food_ordering.controllers.DiningTableController;
import com.group_three.food_ordering.dto.request.DiningTableRequestDto;
import com.group_three.food_ordering.dto.response.DiningTableResponseDto;
import com.group_three.food_ordering.enums.DiningTableStatus;
import com.group_three.food_ordering.services.DiningTableService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DiningTableControllerImpl implements DiningTableController {

    private final DiningTableService diningTableService;

    @PreAuthorize("hasAnyRole('ADMIN','ROOT')")
    @Override
    public ResponseEntity<DiningTableResponseDto> createTable(
            DiningTableRequestDto diningTableRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(diningTableService.create(diningTableRequestDto));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER','ROOT')")
    @Override
    public ResponseEntity<Page<DiningTableResponseDto>> getTables(Pageable pageable) {
        return ResponseEntity.ok(diningTableService.getAll(pageable));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER','ROOT')")
    @Override
    public ResponseEntity<DiningTableResponseDto> getTableById(
            UUID id) {
        return ResponseEntity.ok(diningTableService.getById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER','ROOT')")
    @Override
    public ResponseEntity<DiningTableResponseDto> getTableByNumber(
            Integer number) {
        return ResponseEntity.ok(diningTableService.getByNumber(number));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER','ROOT')")
    @Override
    public ResponseEntity<Page<DiningTableResponseDto>> getFilteredTables(
            DiningTableStatus status,
            Integer capacity,
            Pageable pageable) {
        return ResponseEntity.ok(diningTableService.getByFilters(status, capacity, pageable));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER','ROOT')")
    @Override
    public ResponseEntity<Void> update(
            DiningTableStatus status,
            UUID id) {
        diningTableService.updateStatus(status, id);
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER','ROOT')")
    @Override
    public ResponseEntity<DiningTableResponseDto> patch(
            DiningTableRequestDto diningTableRequestDto, UUID id) {
        return ResponseEntity.ok(diningTableService.update(diningTableRequestDto, id));
    }

    @PreAuthorize("hasAnyRole('ADMIN','ROOT')")
    @Override
    public ResponseEntity<Void> delete(UUID id) {
        diningTableService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
