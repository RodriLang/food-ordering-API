package com.group_three.food_ordering.services;

import com.group_three.food_ordering.dto.create.TableSessionCreateDto;
import com.group_three.food_ordering.dto.response.InitSessionResponseDto;
import com.group_three.food_ordering.dto.response.TableSessionResponseDto;
import com.group_three.food_ordering.dto.update.TableSessionUpdateDto;
import com.group_three.food_ordering.models.TableSession;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface TableSessionService {

    InitSessionResponseDto enter(TableSessionCreateDto tableSessionCreateDto);

    List<TableSessionResponseDto> getAll();

    TableSessionResponseDto getById(UUID sessionId);

    TableSession getEntityById(UUID sessionId);

    List<TableSessionResponseDto> getByFoodVenueAndTable(UUID foodVenueId, Integer tableNumber);

    List<TableSessionResponseDto> getByContextAndTable(Integer tableNumber);

    List<TableSessionResponseDto> getByTableAndTimeRange(Integer tableNumber, LocalDateTime start, LocalDateTime end);

    List<TableSessionResponseDto> getActiveSessions();

    List<TableSessionResponseDto> getByHostClient(UUID clientId);

    List<TableSessionResponseDto> getByAuthUserHostClient();

    List<TableSessionResponseDto> getPastByParticipant(UUID clientId);

    List<TableSessionResponseDto> getPastByAuthUserParticipant();

    TableSessionResponseDto getLatestByTable(UUID tableId);

    TableSessionResponseDto update(TableSessionUpdateDto tableSessionUpdateDto, UUID id);

    TableSessionResponseDto addClient(UUID sessionId, UUID clientId);

    TableSessionResponseDto closeSession(UUID tableId);
}
