package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.dtos.create.TableCreateDto;
import com.group_three.food_ordering.dtos.response.TableResponseDto;
import com.group_three.food_ordering.dtos.update.TableUpdateDto;
import com.group_three.food_ordering.services.interfaces.ITableService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tables")
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

    @GetMapping("/{id}")
    public ResponseEntity<TableResponseDto> getTableById(@PathVariable Long id) {
        return ResponseEntity.ok(tableService.getById(id));
    }

    @PutMapping
    public ResponseEntity<TableResponseDto> update(@RequestBody @Valid TableUpdateDto tableUpdateDto) {
        return ResponseEntity.ok(tableService.update(tableUpdateDto));
    }

    @PatchMapping
    public ResponseEntity<TableResponseDto> patch(@RequestBody @Valid TableUpdateDto tableUpdateDto) {
        return ResponseEntity.ok(tableService.update(tableUpdateDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTable(@PathVariable Long id) {
        tableService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
