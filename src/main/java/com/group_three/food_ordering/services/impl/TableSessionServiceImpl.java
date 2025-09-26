package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.context.TenantContext;
import com.group_three.food_ordering.dto.create.TableSessionCreateDto;
import com.group_three.food_ordering.dto.response.AuthResponse;
import com.group_three.food_ordering.dto.response.TableSessionResponseDto;
import com.group_three.food_ordering.dto.update.TableSessionUpdateDto;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.mappers.TableSessionMapper;
import com.group_three.food_ordering.models.*;
import com.group_three.food_ordering.repositories.TableRepository;
import com.group_three.food_ordering.repositories.TableSessionRepository;
import com.group_three.food_ordering.services.AuthService;
import com.group_three.food_ordering.services.ClientService;
import com.group_three.food_ordering.services.TableSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TableSessionServiceImpl implements TableSessionService {

    private final TableSessionRepository tableSessionRepository;
    private final TableSessionMapper tableSessionMapper;
    private final ClientService clientService;
    private final TenantContext tenantContext;;

    private final TableRepository tableRepository;
    private final AuthService authService;
    private final AuthServiceImpl authServiceImpl;

    @Override
    public AuthResponse create(TableSessionCreateDto tableSessionCreateDto) {

        log.debug("[TableSession] Initializing table session tableId={}...", tableSessionCreateDto.getTableId()); // antes de persistir
        TableSession tableSession = tableSessionMapper.toEntity(tableSessionCreateDto);

        Table table = tableRepository.findById(tableSessionCreateDto.getTableId())
                .orElseThrow(() -> new EntityNotFoundException("Table", tableSessionCreateDto.getTableId().toString()));

        FoodVenue foodVenue = table.getFoodVenue();

        tenantContext.setCurrentFoodVenueId(foodVenue.getId().toString());

        tableSession.setFoodVenue(foodVenue);
        tableSession.setTable(table);

        Participant hostParticipant;

        try {
            hostParticipant = authServiceImpl.getCurrentClient();

            tableSession.setSessionHost(hostParticipant);
            tableSession.getParticipants().add(hostParticipant);


        } catch (EntityNotFoundException e) {
            hostParticipant = clientService.getEntityById(UUID.fromString("11111111-0000-4437-96fc-da7e8f0e5a4a"));
        }
        AuthResponse authResponse = authService.initTableSession(hostParticipant.getUser(), foodVenue.getId(), tableSession.getId());

        tableSessionMapper.toDTO(tableSessionRepository.save(tableSession));

        log.info("[TableSession] Initialized entity successfully tableId={}...tableSessionId={}", table.getId(), tableSession.getId());
        return authResponse;
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
    public List<TableSessionResponseDto> getByFoodVenueAndTable(UUID foodVenueId, Integer tableNumber) {
        return tableSessionRepository.findByFoodVenueIdAndTableNumber(foodVenueId, tableNumber).stream()
                .map(tableSessionMapper::toDTO)
                .toList();
    }

    @Override
    public List<TableSessionResponseDto> getByContextAndTable(Integer tableNumber) {

        UUID foodVenueId = tenantContext.getCurrentFoodVenue().getId();

        return tableSessionRepository.findByFoodVenueIdAndTableNumber(foodVenueId, tableNumber).stream()
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
        return tableSessionRepository.findByFoodVenueIdAndSessionHostId(
                        tenantContext.getCurrentFoodVenueId(), clientId).stream()
                .map(tableSessionMapper::toDTO)
                .toList();
    }

    @Override
    public List<TableSessionResponseDto> getByAuthUserHostClient() {

        UUID authClientId = tenantContext.getCurrentFoodVenue().getId();

        return tableSessionRepository.findByFoodVenueIdAndSessionHostId(
                        tenantContext.getCurrentFoodVenueId(), authClientId).stream()
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
    public List<TableSessionResponseDto> getPastByAuthUserParticipant() {

        UUID authClientId = tenantContext.getCurrentFoodVenue().getId();

        return tableSessionRepository.findPastSessionsByParticipantId(
                        tenantContext.getCurrentFoodVenueId(), authClientId).stream()
                .map(tableSessionMapper::toDTO)
                .toList();
    }

    @Override
    public TableSessionResponseDto getLatestByTable(UUID tableId) {
        TableSession tableSession = tableSessionRepository.findTopByFoodVenueIdAndTableIdOrderByStartTimeDesc(
                        tenantContext.getCurrentFoodVenueId(), tableId)
                .orElseThrow(() -> new EntityNotFoundException("TableSession Not Found"));
        return tableSessionMapper.toDTO(tableSession);
    }

    @Override
    public TableSessionResponseDto update(TableSessionUpdateDto tableSessionUpdateDto, UUID id) {
        TableSession tableSession = this.findById(id);

        if (tableSessionUpdateDto.getEndTime() != null) {
            tableSession.setEndTime(tableSessionUpdateDto.getEndTime());
        }

        if (tableSessionUpdateDto.getParticipantIds() != null) {
            List<Participant> participants = new ArrayList<>();

            for (UUID participantId : tableSessionUpdateDto.getParticipantIds()) {
                Participant participant = clientService.getEntityById(participantId);
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

        Participant participant = clientService.getEntityById(clientId);

        tableSession.getParticipants().add(participant);

        TableSession updatedTableSession = tableSessionRepository.save(tableSession);

        return tableSessionMapper.toDTO(updatedTableSession);
    }


    @Override
    public TableSessionResponseDto joinSession(UUID tableId) {
        Participant participant = authServiceImpl.getCurrentClient();
        TableSession session = authServiceImpl.getCurrentTableSession();

        session.getParticipants().add(participant);
        tableSessionRepository.save(session);
        return null;
    }

    @Override
    public TableSessionResponseDto closeSession(UUID tableId) {
        return null;
    }

    private TableSession findById(UUID id) {
        return tableSessionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("TableSession", id.toString()));
    }
}
