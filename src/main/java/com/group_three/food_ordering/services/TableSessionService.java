package com.group_three.food_ordering.services;

import com.group_three.food_ordering.dto.request.TableSessionRequestDto;
import com.group_three.food_ordering.dto.response.AuthResponse;
import com.group_three.food_ordering.dto.response.TableSessionResponseDto;
import com.group_three.food_ordering.models.TableSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.Instant;
import java.util.UUID;

public interface TableSessionService {

    AuthResponse enter(TableSessionRequestDto tableSessionRequestDto);

    AuthResponse leaveCurrentSession();

    AuthResponse closeCurrentSession();

    Page<TableSessionResponseDto> getAll(Pageable pageable);

    TableSessionResponseDto getById(UUID sessionId);

    TableSessionResponseDto getByCurrentParticipant();

    TableSession getEntityById(UUID sessionId);

    Page<TableSessionResponseDto> getByFoodVenueAndTable(UUID foodVenueId, Integer tableNumber, Pageable pageable);

    Page<TableSessionResponseDto> getByContextAndTable(Integer tableNumber, Pageable pageable);

    Page<TableSessionResponseDto> getByTableAndTimeRange(Integer tableNumber, Instant start, Instant end, Pageable pageable);

    Page<TableSessionResponseDto> getActiveSessions(Pageable pageable);

    Page<TableSessionResponseDto> getByHostClient(UUID clientId, Pageable pageable);

    Page<TableSessionResponseDto> getByAuthUserHostClient(Pageable pageable);

    Page<TableSessionResponseDto> getPastByParticipant(UUID clientId, Pageable pageable);

    Page<TableSessionResponseDto> getPastByAuthUserParticipant(Pageable pageable);

    TableSessionResponseDto getLatestByTable(UUID tableId);

    TableSessionResponseDto addClient(UUID sessionId, UUID clientId);

    void closeSessionByTable(UUID tableId);
}
