package com.group_three.food_ordering.services.interfaces;

import com.group_three.food_ordering.dtos.create.TableSessionCreateDto;
import com.group_three.food_ordering.dtos.response.TableSessionResponseDto;
import com.group_three.food_ordering.dtos.update.TableSessionUpdateDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface ITableSessionService {

    TableSessionResponseDto create(TableSessionCreateDto tableSessionCreateDto);
    List<TableSessionResponseDto> getAll();
    TableSessionResponseDto getById(UUID sessionId);
    List<TableSessionResponseDto> getByTable(Integer tableNumber);
    List<TableSessionResponseDto> getByTableAndTimeRange(Integer tableNumber, LocalDateTime start, LocalDateTime end);
    List<TableSessionResponseDto> getActiveSessions();
    List<TableSessionResponseDto> getByHostClient(UUID clientId);
    List<TableSessionResponseDto> getPastByParticipant(UUID clientId);
    TableSessionResponseDto getLatestByTable(UUID tableId);
    TableSessionResponseDto update(TableSessionUpdateDto tableSessionUpdateDto, UUID id);
    TableSessionResponseDto addClient(UUID sessionId, UUID clientId);
}
