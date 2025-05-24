package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.context.TenantContext;
import com.group_three.food_ordering.dtos.create.TableCreateDto;
import com.group_three.food_ordering.dtos.create.TableSessionCreateDto;
import com.group_three.food_ordering.dtos.response.TableSessionResponseDto;
import com.group_three.food_ordering.dtos.update.TableSessionUpdateDto;
import com.group_three.food_ordering.exceptions.TableSessionNotFoundException;
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
import java.util.ArrayList;
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
        tableSession.setHostClient(hostClient);

        List<Client> participants = new ArrayList<>();
        participants.add(hostClient);

        tableSession.setParticipants(participants);

        return tableSessionMapper.toDTO(tableSessionRepository.save(tableSession));
    }

    @Override
    public List<TableSessionResponseDto> getAll() {
        return tableSessionRepository.findByFoodVenueId(tenantContext.getCurrentFoodVenue().getId()).stream()
                .map(tableSessionMapper::toDTO)
                .toList();
    }

    @Override
    public TableSessionResponseDto getById(UUID id) {
        TableSession tableSession = tableSessionRepository.findById(id)
                .orElseThrow(TableSessionNotFoundException::new);
        return tableSessionMapper.toDTO(tableSession);
    }

    @Override
    public List<TableSessionResponseDto> getByTable(Integer tableNumber) {
        return tableSessionRepository.findByFoodVenueIdAndTableNumber(tenantContext.getCurrentFoodVenue().getId(), tableNumber).stream()
                .map(tableSessionMapper::toDTO)
                .toList();
    }

    @Override
    public List<TableSessionResponseDto> getByTableAndTimeRange(Integer tableNumber, LocalDateTime start, LocalDateTime end) {
        LocalDateTime effectiveEnd = (end == null) ? LocalDateTime.now() : end;

        if (start.isAfter(effectiveEnd)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }

        return tableSessionRepository
                .findByFoodVenueIdAndTableNumberAndEndTimeGreaterThanEqualAndStartTimeLessThanEqual(
                        tenantContext.getCurrentFoodVenue().getId(), tableNumber, start, effectiveEnd)
                .stream()
                .map(tableSessionMapper::toDTO)
                .toList();
    }

    @Override
    public List<TableSessionResponseDto> getActiveSessions() {
        return tableSessionRepository
                .findByFoodVenueIdAndEndTimeIsNull(tenantContext.getCurrentFoodVenue().getId()).stream()
                .map(tableSessionMapper::toDTO)
                .toList();
    }

    @Override
    public List<TableSessionResponseDto> getByHostClient(UUID clientId) {
        return tableSessionRepository.findByFoodVenueIdAndHostClientId(tenantContext.getCurrentFoodVenue().getId(), clientId).stream()
                .map(tableSessionMapper::toDTO)
                .toList();
    }

    @Override
    public List<TableSessionResponseDto> getPastByParticipant(UUID clientId) {
        return tableSessionRepository.findPastSessionsByParticipantId(tenantContext.getCurrentFoodVenue().getId(), clientId).stream()
                .map(tableSessionMapper::toDTO)
                .toList();
    }

    @Override
    public TableSessionResponseDto getLatestByTable(UUID tableId) {
        TableSession tableSession = tableSessionRepository.findTopByFoodVenueIdAndTableIdOrderByStartTimeDesc(tenantContext.getCurrentFoodVenue().getId(), tableId)
                .orElseThrow(TableSessionNotFoundException::new);
        return tableSessionMapper.toDTO(tableSession);
    }

    @Override
    public TableSessionResponseDto update(TableSessionUpdateDto tableSessionUpdateDto, UUID id) {
        TableSession tableSession = tableSessionRepository.findById(id)
                .orElseThrow(TableSessionNotFoundException::new);

        if (tableSessionUpdateDto.getEndTime() != null) {
            tableSession.setEndTime(tableSessionUpdateDto.getEndTime());
        }

        if (tableSessionUpdateDto.getParticipantIds() != null) {
            List<Client> participants = new ArrayList<>();

            for (UUID participantId : tableSessionUpdateDto.getParticipantIds()) {
                Client participant = clientService.getEntityById(participantId);
                participants.add(participant);
            }
            tableSession.setParticipants(participants);
        }

        TableSession updatedTableSession = tableSessionRepository.save(tableSession);

        return tableSessionMapper.toDTO(updatedTableSession);
    }

    @Override
    public TableSessionResponseDto addClient(UUID sessionId, UUID clientId) {
        TableSession tableSession = tableSessionRepository.findById(sessionId)
                .orElseThrow(TableSessionNotFoundException::new);

        Client client = clientService.getEntityById(clientId);

        tableSession.getParticipants().add(client);

        TableSession updatedTableSession = tableSessionRepository.save(tableSession);

        return tableSessionMapper.toDTO(updatedTableSession);
    }
}
