package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.context.TenantContext;
import com.group_three.food_ordering.dtos.create.TableSessionCreateDto;
import com.group_three.food_ordering.dtos.response.TableSessionResponseDto;
import com.group_three.food_ordering.mappers.TableSessionMapper;
import com.group_three.food_ordering.models.Client;
import com.group_three.food_ordering.models.FoodVenue;
import com.group_three.food_ordering.models.Table;
import com.group_three.food_ordering.models.TableSession;
import com.group_three.food_ordering.repositories.ITableSessionRepository;
import com.group_three.food_ordering.services.interfaces.IClientService;
import com.group_three.food_ordering.services.interfaces.ITableSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TableSessionService implements ITableSessionService {

    private final ITableSessionRepository tableSessionRepository;
    private final TableSessionMapper tableSessionMapper;
    private final TableService tableService;
    private final IClientService clientService;
    private final TenantContext tenantContext;

    @Override
    public TableSessionResponseDto create(TableSessionCreateDto tableSessionCreateDto) {
        TableSession tableSession = tableSessionMapper.toEntity(tableSessionCreateDto);

        FoodVenue foodVenue = new FoodVenue();
        foodVenue.setId(tenantContext.getCurrentFoodVenue().getId());
        tableSession.setFoodVenue(foodVenue);

        Table table = tableService.getEntityById(tableSessionCreateDto.getTableId());

        tableSession.setTable(table);
        tableSession.setStartTime(LocalDateTime.now());
        tableSession.setEndTime(null);

        Client hostClient = clientService.getEntityById(tableSessionCreateDto.getHostClientId());
        return null;
    }

    @Override
    public TableSessionResponseDto getById(UUID sessionId) {
        return null;
    }

    @Override
    public List<TableSessionResponseDto> getByTable(UUID tableId) {
        return List.of();
    }

    @Override
    public List<TableSessionResponseDto> getRecentByTable(UUID tableId, int daysBack) {
        return List.of();
    }

    @Override
    public List<TableSessionResponseDto> getActiveByTable(UUID tableId) {
        return List.of();
    }

    @Override
    public TableSessionResponseDto getLastByTable(UUID tableId) {
        return null;
    }

    @Override
    public TableSessionResponseDto closeSession(UUID sessionId) {
        return null;
    }
}
