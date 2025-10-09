package com.group_three.food_ordering.controllers.impl;

import com.group_three.food_ordering.controllers.TableSessionController;
import com.group_three.food_ordering.dto.request.TableSessionRequestDto;
import com.group_three.food_ordering.dto.response.InitSessionResponseDto;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
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

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER')")
    @Override
    public ResponseEntity<PageResponse<TableSessionResponseDto>> getTableSessionsByContext(Pageable pageable) {
        return ResponseEntity.ok(PageResponse.of(tableSessionService.getAll(pageable)));
    }

    @PreAuthorize("hasRole('ROOT')")
    @Override
    public ResponseEntity<PageResponse<TableSessionResponseDto>> getTableSessionsByFoodVenueId(Pageable pageable) {
        return ResponseEntity.ok(PageResponse.of(tableSessionService.getAll(pageable)));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER', 'ROOT')")
    @Override
    public ResponseEntity<TableSessionResponseDto> getTableSessionById(
            UUID id) {
        return ResponseEntity.ok(tableSessionService.getById(id));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER', 'ROOT')")
    @Override
    public ResponseEntity<PageResponse<TableSessionResponseDto>> getTableSessionsByContextAndTable(
            Integer tableNumber, Pageable pageable) {
        return ResponseEntity.ok(PageResponse.of(tableSessionService.getByContextAndTable(tableNumber, pageable)));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER', 'ROOT')")
    @Override
    public ResponseEntity<PageResponse<TableSessionResponseDto>> getTableSessionsByTableAndTimeRange(
            Integer tableNumber,
            LocalDateTime start,
            LocalDateTime end,
            Pageable pageable) {
        return ResponseEntity.ok(PageResponse.of(
                tableSessionService.getByTableAndTimeRange(tableNumber, start, end, pageable)));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER', 'ROOT')")
    @Override
    public ResponseEntity<PageResponse<TableSessionResponseDto>> getActiveSessions(Pageable pageable) {
        return ResponseEntity.ok(PageResponse.of(tableSessionService.getActiveSessions(pageable)));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER', 'ROOT')")
    @Override
    public ResponseEntity<PageResponse<TableSessionResponseDto>> getTableSessionsByHostClient(
            UUID clientId, Pageable pageable) {
        return ResponseEntity.ok(PageResponse.of(
                tableSessionService.getByHostClient(clientId, pageable)));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER', 'ROOT')")
    @Override
    public ResponseEntity<PageResponse<TableSessionResponseDto>> getPastTableSessionsByParticipant(
            UUID clientId, Pageable pageable) {
        return ResponseEntity.ok(PageResponse.of(
                tableSessionService.getPastByParticipant(clientId, pageable)));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER', 'ROOT')")
    @Override
    public ResponseEntity<TableSessionResponseDto> getLatestTableSessionByTable(
            UUID tableId) {
        return ResponseEntity.ok(tableSessionService.getLatestByTable(tableId));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER', 'ROOT')")
    @Override
    public ResponseEntity<TableSessionResponseDto> addClientToSession(
            UUID id,
            UUID clientId) {
        return ResponseEntity.ok(tableSessionService.addClient(id, clientId));
    }

    @PreAuthorize("hasAnyRole('CLIENT', 'GUEST', 'STAFF', 'ADMIN', 'ROOT')")
    @Override
    public ResponseEntity<PageResponse<OrderResponseDto>> getOrdersByTableSession(
            UUID id,
            OrderStatus status,
            Pageable pageable) {
        return ResponseEntity.ok(PageResponse.of(
                orderService.getOrdersByTableSessionAndStatus(id, status, pageable)));
    }

    @PreAuthorize("hasAnyRole('CLIENT', 'GUEST')")
    @Override
    public ResponseEntity<Void> endYourOwnTableSession() {
        tableSessionService.closeCurrentSession();
        return ResponseEntity.ok().build();
    }

    @PreAuthorize("hasAnyRole('STAFF', 'ADMIN', 'ROOT')")
    @Override
    public ResponseEntity<Void> endTableSessionById(UUID tableSessionId) {
        tableSessionService.closeSessionById(tableSessionId);
        return ResponseEntity.ok().build();
    }
}
