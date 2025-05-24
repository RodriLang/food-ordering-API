package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dtos.create.TableSessionCreateDto;
import com.group_three.food_ordering.dtos.response.TableSessionResponseDto;
import com.group_three.food_ordering.dtos.update.TableSessionUpdateDto;
import com.group_three.food_ordering.services.interfaces.ITableSessionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(ApiPaths.TABLE_SESSION_BASE)
@RequiredArgsConstructor
public class TableSessionController {

    private final ITableSessionService tableSessionService;

    @PostMapping
    public ResponseEntity<TableSessionResponseDto> createTableSession(@RequestBody @Valid TableSessionCreateDto tableSessionCreateDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tableSessionService.create(tableSessionCreateDto));
    }

    @GetMapping
    public ResponseEntity<List<TableSessionResponseDto>> getTableSessions() {
        return ResponseEntity.ok(tableSessionService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TableSessionResponseDto> getTableSessionById(@PathVariable UUID id) {
        return ResponseEntity.ok(tableSessionService.getById(id));
    }

    @GetMapping("/table/{tableNumber}")
    public ResponseEntity<List<TableSessionResponseDto>> getTableSessionsByTable(@PathVariable Integer tableNumber) {
        return ResponseEntity.ok(tableSessionService.getByTable(tableNumber));
    }

    @GetMapping("/table/{tableNumber}/time-range")
    public ResponseEntity<List<TableSessionResponseDto>> getTableSessionsByTableAndTimeRange(
            @PathVariable Integer tableNumber,
            @RequestParam(value = "start") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(value = "end", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        return ResponseEntity.ok(tableSessionService.getByTableAndTimeRange(tableNumber, start, end));
    }

    @GetMapping("/active")
    public ResponseEntity<List<TableSessionResponseDto>> getActiveSessions() {
        return ResponseEntity.ok(tableSessionService.getActiveSessions());
    }

    @GetMapping("/host/{clientId}")
    public ResponseEntity<List<TableSessionResponseDto>> getTableSessionsByHostClient(@PathVariable UUID clientId) {
        return ResponseEntity.ok(tableSessionService.getByHostClient(clientId));
    }

    @GetMapping("/participant/{clientId}")
    public ResponseEntity<List<TableSessionResponseDto>> getPastTableSessionsByParticipant(@PathVariable UUID clientId) {
        return ResponseEntity.ok(tableSessionService.getPastByParticipant(clientId));
    }

    @GetMapping("/latest/{tableId}")
    public ResponseEntity<TableSessionResponseDto> getLatestTableSessionByTable(@PathVariable UUID tableId) {
        return ResponseEntity.ok(tableSessionService.getLatestByTable(tableId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TableSessionResponseDto> update(@RequestBody @Valid TableSessionUpdateDto tableSessionUpdateDto, @PathVariable UUID id) {
        return ResponseEntity.ok(tableSessionService.update(tableSessionUpdateDto, id));
    }

    @PutMapping("/{id}/clients/{clientId}")
    public ResponseEntity<TableSessionResponseDto> addClientToSession(
            @PathVariable UUID id,
            @PathVariable UUID clientId) {
        return ResponseEntity.ok(tableSessionService.addClient(id, clientId));
    }
}
