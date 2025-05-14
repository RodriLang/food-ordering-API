package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dtos.create.TableCreateDto;
import com.group_three.food_ordering.dtos.response.TableResponseDto;
import com.group_three.food_ordering.dtos.update.TableUpdateDto;
import com.group_three.food_ordering.enums.TableStatus;
import com.group_three.food_ordering.services.interfaces.ITableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.TABLE_BASE)
@RequiredArgsConstructor
public class TableController {

    private final ITableService tableService;

    @PostMapping
    public ResponseEntity<TableResponseDto> createTable(@RequestBody @Valid TableCreateDto tableCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tableService.create(tableCreateDto));
    }

    @GetMapping
    public ResponseEntity<List<TableResponseDto>> getTables() {
        return ResponseEntity.ok(tableService.getAll());
    }

    @GetMapping("/status/{tableStatus}")
    public ResponseEntity<List<TableResponseDto>> getTablesByStatus(@PathVariable TableStatus tableStatus) {
        return ResponseEntity.ok(tableService.getAllByStatus(tableStatus));
    }

    @GetMapping("/number/{tableNumber}")
    public ResponseEntity<TableResponseDto> getTableByNumber(@PathVariable Integer tableNumber) {
        return ResponseEntity.ok(tableService.getByNumber(tableNumber));
    }

    @PutMapping("/{tableId}")
    public ResponseEntity<TableResponseDto> update(@RequestBody @Valid TableUpdateDto tableUpdateDto, @PathVariable Long tableId) {
        return ResponseEntity.ok(tableService.update(tableUpdateDto, tableId));
    }

    @PatchMapping("/{tableId}")
    public ResponseEntity<TableResponseDto> patch(@RequestBody @Valid TableUpdateDto tableUpdateDto, @PathVariable Long tableId) {
        return ResponseEntity.ok(tableService.update(tableUpdateDto, tableId));
    }

    @DeleteMapping("/{tableId}")
    public ResponseEntity<Void> deleteTable(@PathVariable Long tableId) {
        tableService.delete(tableId);
        return ResponseEntity.noContent().build();
    }
}
