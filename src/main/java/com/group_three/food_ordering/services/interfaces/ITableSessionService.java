package com.group_three.food_ordering.services.interfaces;

import com.group_three.food_ordering.dtos.create.TableSessionCreateDto;
import com.group_three.food_ordering.dtos.response.TableSessionResponseDto;

import java.util.List;
import java.util.UUID;

public interface ITableSessionService {

    TableSessionResponseDto create(TableSessionCreateDto tableSessionCreateDto);
    TableSessionResponseDto getById(UUID sessionId);
    List<TableSessionResponseDto> getByTable(UUID tableId);
    List<TableSessionResponseDto> getRecentByTable(UUID tableId, int daysBack);
    List<TableSessionResponseDto> getActiveByTable(UUID tableId);
    TableSessionResponseDto getLastByTable(UUID tableId);
    TableSessionResponseDto closeSession(UUID sessionId);
}
