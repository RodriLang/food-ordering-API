package com.group_three.food_ordering.controllers.impl;

import com.group_three.food_ordering.controllers.TableSessionController;
import com.group_three.food_ordering.dto.request.TableSessionRequestDto;
import com.group_three.food_ordering.dto.response.AuthResponse;
import com.group_three.food_ordering.dto.response.OrderResponseDto;
import com.group_three.food_ordering.dto.response.PageResponse;
import com.group_three.food_ordering.dto.response.TableSessionResponseDto;
import com.group_three.food_ordering.enums.OrderStatus;
import com.group_three.food_ordering.services.OrderService;
import com.group_three.food_ordering.services.TableSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class TableSessionControllerImpl implements TableSessionController {

    private final TableSessionService tableSessionService;
    private final OrderService orderService;

    @Override
    public ResponseEntity<AuthResponse> createTableSession(
            TableSessionRequestDto tableSessionRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).
                body(tableSessionService.enter(tableSessionRequestDto));
    }

    @Override
    public ResponseEntity<PageResponse<TableSessionResponseDto>> getTableSessionsByContext(Pageable pageable) {
        return ResponseEntity.ok(PageResponse.of(tableSessionService.getAll(pageable)));
    }

    @Override
    public ResponseEntity<PageResponse<TableSessionResponseDto>> getTableSessionsByFoodVenueId(Pageable pageable) {
        return ResponseEntity.ok(PageResponse.of(tableSessionService.getAll(pageable)));
    }

    @Override
    public ResponseEntity<TableSessionResponseDto> getTableSessionById(
            UUID id) {
        return ResponseEntity.ok(tableSessionService.getById(id));
    }

    @Override
    public ResponseEntity<PageResponse<TableSessionResponseDto>> getTableSessionsByContextAndTable(
            Integer tableNumber, Pageable pageable) {
        return ResponseEntity.ok(PageResponse.of(tableSessionService.getByContextAndTable(tableNumber, pageable)));
    }

    @Override
    public ResponseEntity<PageResponse<TableSessionResponseDto>> getTableSessionsByTableAndTimeRange(
            Integer tableNumber,
            Instant start,
            Instant end,
            Pageable pageable) {
        return ResponseEntity.ok(PageResponse.of(
                tableSessionService.getByTableAndTimeRange(tableNumber, start, end, pageable)));
    }

    @Override
    public ResponseEntity<PageResponse<TableSessionResponseDto>> getActiveSessions(Pageable pageable) {
        return ResponseEntity.ok(PageResponse.of(tableSessionService.getActiveSessions(pageable)));
    }

    @Override
    public ResponseEntity<PageResponse<TableSessionResponseDto>> getTableSessionsByHostClient(
            UUID clientId, Pageable pageable) {
        return ResponseEntity.ok(PageResponse.of(
                tableSessionService.getByHostClient(clientId, pageable)));
    }

    @Override
    public ResponseEntity<PageResponse<TableSessionResponseDto>> getPastTableSessionsByParticipant(
            UUID clientId, Pageable pageable) {
        return ResponseEntity.ok(PageResponse.of(
                tableSessionService.getPastByParticipant(clientId, pageable)));
    }

    @Override
    public ResponseEntity<TableSessionResponseDto> getLatestTableSessionByTable(
            UUID tableId) {
        return ResponseEntity.ok(tableSessionService.getLatestByTable(tableId));
    }

    @Override
    public ResponseEntity<TableSessionResponseDto> addClientToSession(
            UUID id,
            UUID clientId) {
        return ResponseEntity.ok(tableSessionService.addClient(id, clientId));
    }

    @Override
    public ResponseEntity<PageResponse<OrderResponseDto>> getOrdersByTableSession(
            UUID id,
            OrderStatus status,
            Pageable pageable) {
        return ResponseEntity.ok(PageResponse.of(
                orderService.getOrdersByTableSessionAndStatus(id, status, pageable)));
    }

    @Override
    public ResponseEntity<Void> endTableSessionByTable(UUID tableId) {
        tableSessionService.closeSessionByTable(tableId);
        return ResponseEntity.ok().build();
    }
}
