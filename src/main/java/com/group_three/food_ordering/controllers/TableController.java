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
import java.util.UUID;

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

    @GetMapping("/{id}")
    public ResponseEntity<TableResponseDto> getTableById(@PathVariable UUID id) {
        return ResponseEntity.ok(tableService.getById(id));
    }

    @GetMapping("/number/{number}")
    public ResponseEntity<TableResponseDto> getTableByNumber(@PathVariable Integer number) {
        return ResponseEntity.ok(tableService.getByNumber(number));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<TableResponseDto>> getFilteredTables(@RequestParam(required = false) TableStatus status, @RequestParam(required = false) Integer capacity) {
        return ResponseEntity.ok(tableService.getByFilters(status, capacity));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TableResponseDto> update(@RequestBody @Valid TableUpdateDto tableUpdateDto, @PathVariable UUID id) {
        return ResponseEntity.ok(tableService.update(tableUpdateDto, id));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<TableResponseDto> patch(@RequestBody @Valid TableUpdateDto tableUpdateDto, @PathVariable UUID id) {
        return ResponseEntity.ok(tableService.update(tableUpdateDto, id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        System.out.println(">>> Entró al controller DELETE");
        tableService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
