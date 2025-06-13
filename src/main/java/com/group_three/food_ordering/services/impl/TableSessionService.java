package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.context.TenantContext;
import com.group_three.food_ordering.dtos.create.TableSessionCreateDto;
import com.group_three.food_ordering.dtos.response.AuthResponse;
import com.group_three.food_ordering.dtos.response.TableSessionResponseDto;
import com.group_three.food_ordering.dtos.update.TableSessionUpdateDto;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.mappers.TableSessionMapper;
import com.group_three.food_ordering.models.*;
import com.group_three.food_ordering.repositories.ITableRepository;
import com.group_three.food_ordering.repositories.ITableSessionRepository;
import com.group_three.food_ordering.security.JwtService;
import com.group_three.food_ordering.services.interfaces.IClientService;
import com.group_three.food_ordering.services.interfaces.ITableSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TableSessionService implements ITableSessionService {

    private final ITableSessionRepository tableSessionRepository;
    private final TableSessionMapper tableSessionMapper;
    private final IClientService clientService;
    private final TenantContext tenantContext;

    private final ITableRepository tableRepository;
    private final JwtService jwtService;
    private final AuthService authService;

    @Override
    public AuthResponse create(TableSessionCreateDto tableSessionCreateDto) {
        TableSession tableSession = tableSessionMapper.toEntity(tableSessionCreateDto);

        Table table = tableRepository.findById(tableSessionCreateDto.getTableId())
                .orElseThrow(() -> new EntityNotFoundException("Table", tableSessionCreateDto.getTableId().toString()));

        FoodVenue foodVenue = table.getFoodVenue();

        tenantContext.setCurrentFoodVenueId(foodVenue.getId().toString());

        tableSession.setFoodVenue(foodVenue);
        tableSession.setTable(table);
        tableSession.setStartTime(LocalDateTime.now());
        tableSession.setEndTime(null);
        tableSession.setId(UUID.randomUUID());

        Client hostClient = authService.getCurrentClient();

        tableSession.setHostClient(hostClient);

        List<Client> participants = new ArrayList<>();
        participants.add(hostClient);

        tableSession.setParticipants(participants);

        AuthResponse response = AuthResponse.builder()
                .token(jwtService.generateToken(hostClient.getUser().getEmail(),
                        foodVenue.getId(),
                        hostClient.getUser().getRole().name(),
                        tableSession.getId(),
                        hostClient.getId()))
                .build();

        tableSessionMapper.toDTO(tableSessionRepository.save(tableSession));

        return response;
    }

    @Override
    public List<TableSessionResponseDto> getAll() {
        return tableSessionRepository.findByFoodVenueId(tenantContext.getCurrentFoodVenue().getId()).stream()
                .map(tableSessionMapper::toDTO)
                .toList();
    }

    @Override
    public TableSessionResponseDto getById(UUID id) {
        TableSession tableSession = this.findById(id);
        return tableSessionMapper.toDTO(tableSession);
    }

    @Override
    public List<TableSessionResponseDto> getByTable(Integer tableNumber) {
        return tableSessionRepository.findByFoodVenueIdAndTableNumber(
                tenantContext.getCurrentFoodVenue().getId(), tableNumber).stream()
                .map(tableSessionMapper::toDTO)
                .toList();
    }

    @Override
    public List<TableSessionResponseDto> getByTableAndTimeRange(
            Integer tableNumber, LocalDateTime start, LocalDateTime end) {
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
        return tableSessionRepository.findByFoodVenueIdAndHostClientId(
                tenantContext.getCurrentFoodVenueId(), clientId).stream()
                .map(tableSessionMapper::toDTO)
                .toList();
    }

    @Override
    public List<TableSessionResponseDto> getPastByParticipant(UUID clientId) {
        return tableSessionRepository.findPastSessionsByParticipantId(
                tenantContext.getCurrentFoodVenueId(), clientId).stream()
                .map(tableSessionMapper::toDTO)
                .toList();
    }

    @Override
    public TableSessionResponseDto getLatestByTable(UUID tableId) {
        TableSession tableSession = tableSessionRepository.findTopByFoodVenueIdAndTableIdOrderByStartTimeDesc(
                tenantContext.getCurrentFoodVenueId(), tableId)
                .orElseThrow(()-> new EntityNotFoundException("TableSession Not Found"));
        return tableSessionMapper.toDTO(tableSession);
    }

    @Override
    public TableSessionResponseDto update(TableSessionUpdateDto tableSessionUpdateDto, UUID id) {
        TableSession tableSession = this.findById(id);

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
        TableSession tableSession = this.findById(sessionId);

        Client client = clientService.getEntityById(clientId);

        tableSession.getParticipants().add(client);

        TableSession updatedTableSession = tableSessionRepository.save(tableSession);

        return tableSessionMapper.toDTO(updatedTableSession);
    }


    @Override
    public TableSessionResponseDto joinSession(UUID tableId) {
        Client client = authService.getCurrentClient();
        TableSession session = authService.getCurrentTableSession();

        session.getParticipants().add(client);
        tableSessionRepository.save(session);
        return null;
    }

    @Override
    public TableSessionResponseDto closeSession(UUID tableId) {
        return null;
    }

    private TableSession findById(UUID id) {
      return  tableSessionRepository.findById(id)
              .orElseThrow(()-> new EntityNotFoundException("TableSession", id.toString()));
    }
}
