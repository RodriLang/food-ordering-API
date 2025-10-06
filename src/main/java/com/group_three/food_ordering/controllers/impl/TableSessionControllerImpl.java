package com.group_three.food_ordering.controllers.impl;

import com.group_three.food_ordering.controllers.TableSessionController;
import com.group_three.food_ordering.dto.request.TableSessionRequestDto;
import com.group_three.food_ordering.dto.response.InitSessionResponseDto;
import com.group_three.food_ordering.dto.response.OrderResponseDto;
import com.group_three.food_ordering.dto.response.TableSessionResponseDto;
import com.group_three.food_ordering.enums.OrderStatus;
import com.group_three.food_ordering.services.OrderService;
import com.group_three.food_ordering.services.TableSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TableSessionControllerImpl implements TableSessionController {

    private final TableSessionService tableSessionService;
    private final OrderService orderService;

    @PreAuthorize("hasRole('CLIENT') or isAnonymous()")
    @Override
    public ResponseEntity<InitSessionResponseDto> createTableSession(
            TableSessionRequestDto tableSessionRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).
                body(tableSessionService.enter(tableSessionRequestDto));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'SUPER_ADMIN')")
    @Override
    public ResponseEntity<List<TableSessionResponseDto>> getTableSessionsByContext() {
        return ResponseEntity.ok(tableSessionService.getAll());
    }

    @PreAuthorize("hasRole('ROOT')")
    @Override
    public ResponseEntity<List<TableSessionResponseDto>> getTableSessionsByFoodVenueId() {
        return ResponseEntity.ok(tableSessionService.getAll());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'SUPER_ADMIN','ROOT')")
    @Override
    public ResponseEntity<TableSessionResponseDto> getTableSessionById(
            UUID id) {
        return ResponseEntity.ok(tableSessionService.getById(id));
    }

    @PreAuthorize("hasRole('ROOT')")
    @Override
    public ResponseEntity<List<TableSessionResponseDto>> getTableSessionsByFoodVenueAndTable(
            Integer tableNumber,
            UUID foodVenueId) {
        return ResponseEntity.ok(tableSessionService.getByFoodVenueAndTable(foodVenueId, tableNumber));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'SUPER_ADMIN')")
    @Override
    public ResponseEntity<List<TableSessionResponseDto>> getTableSessionsByContextAndTable(
            Integer tableNumber) {
        return ResponseEntity.ok(tableSessionService.getByContextAndTable(tableNumber));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'SUPER_ADMIN','ROOT')")
    @Override
    public ResponseEntity<List<TableSessionResponseDto>> getTableSessionsByTableAndTimeRange(
            Integer tableNumber,
            LocalDateTime start,
            LocalDateTime end) {
        return ResponseEntity.ok(tableSessionService.getByTableAndTimeRange(tableNumber, start, end));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'SUPER_ADMIN','ROOT')")
    @Override
    public ResponseEntity<List<TableSessionResponseDto>> getActiveSessions() {
        return ResponseEntity.ok(tableSessionService.getActiveSessions());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'SUPER_ADMIN','ROOT')")
    @Override
    public ResponseEntity<List<TableSessionResponseDto>> getTableSessionsByHostClient(
            UUID clientId) {
        return ResponseEntity.ok(tableSessionService.getByHostClient(clientId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'SUPER_ADMIN','ROOT')")
    @Override
    public ResponseEntity<List<TableSessionResponseDto>> getPastTableSessionsByParticipant(UUID clientId) {
        return ResponseEntity.ok(tableSessionService.getPastByParticipant(clientId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'SUPER_ADMIN','ROOT')")
    @Override
    public ResponseEntity<TableSessionResponseDto> getLatestTableSessionByTable(
            UUID tableId) {
        return ResponseEntity.ok(tableSessionService.getLatestByTable(tableId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF','CLIENT','INVITED', 'SUPER_ADMIN','ROOT')")
    @Override
    public ResponseEntity<TableSessionResponseDto> addClientToSession(
            UUID id,
            UUID clientId) {
        return ResponseEntity.ok(tableSessionService.addClient(id, clientId));
    }

    @PreAuthorize("hasAnyRole('CLIENT','INVITED','STAFF','ADMIN','ROOT')")
    @Override
    public ResponseEntity<Page<OrderResponseDto>> getOrdersByTableSession(
            UUID id,
            OrderStatus status,
            Pageable pageable) {
        return ResponseEntity.ok(orderService.getOrdersByTableSessionAndStatus(id, status, pageable));
    }
}
