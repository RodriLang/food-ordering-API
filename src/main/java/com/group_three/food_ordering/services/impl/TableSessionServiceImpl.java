package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.context.TenantContext;
import com.group_three.food_ordering.dto.SessionInfo;
import com.group_three.food_ordering.dto.create.TableSessionCreateDto;
import com.group_three.food_ordering.dto.response.InitSessionResponseDto;
import com.group_three.food_ordering.dto.response.ParticipantResponseDto;
import com.group_three.food_ordering.dto.response.TableSessionResponseDto;
import com.group_three.food_ordering.dto.update.TableSessionUpdateDto;
import com.group_three.food_ordering.enums.TableStatus;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.mappers.ParticipantMapper;
import com.group_three.food_ordering.mappers.TableSessionMapper;
import com.group_three.food_ordering.models.*;
import com.group_three.food_ordering.repositories.TableRepository;
import com.group_three.food_ordering.repositories.TableSessionRepository;
import com.group_three.food_ordering.security.JwtService;
import com.group_three.food_ordering.services.AuthService;
import com.group_three.food_ordering.services.ParticipantService;
import com.group_three.food_ordering.services.TableSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class TableSessionServiceImpl implements TableSessionService {

    private final TableSessionRepository tableSessionRepository;
    private final TableSessionMapper tableSessionMapper;
    private final ParticipantService participantService;
    private final TenantContext tenantContext;
    private final JwtService jwtService;
    private final TableRepository tableRepository;
    private final AuthService authService;
    private final ParticipantMapper participantMapper;

    private static final String ENTITY_NAME = "TableSession";

    @Transactional
    @Override
    public InitSessionResponseDto enter(TableSessionCreateDto tableSessionCreateDto) {
        log.debug("[TableSession] Participant enter to table session tableId={}...", tableSessionCreateDto.getTableId());
        User authUser = authService.getCurrentUser().orElse(null);
        TableSession tableSession = null;

        if (authUser != null) {
            tableSession = verifyActiveTableSessionForAuthUser(authUser);

            if (tableSession != null) {
                Participant curretnparticipant = authService.getCurrentParticipant()
                        .orElseThrow(() -> new EntityNotFoundException("Participant"));
                generateInitSessionResponseDto(tableSession, curretnparticipant);
                InitSessionResponseDto authResponse = generateInitSessionResponseDto(tableSession, tableSession.getSessionHost());

                log.info("[TableSession] Redirect user to active session tableSessionId={}", tableSession.getId());
                return authResponse;
            }
        }
        Table table = tableRepository.findById(tableSessionCreateDto.getTableId())
                .orElseThrow(() -> new EntityNotFoundException("Table", tableSessionCreateDto.getTableId().toString()));

        FoodVenue foodVenue = table.getFoodVenue();
        tenantContext.setCurrentFoodVenueId(foodVenue.getId().toString());


        TableStatus status = table.getStatus();
        switch (status) {
            case AVAILABLE -> tableSession = initSession(table, authUser);
            case OCCUPIED -> tableSession = joinSession(table.getId(), authUser);
            case OUT_OF_SERVICE ->
                    throw new IllegalStateException("Table is out of service, cannot start or join a session.");
            case COMPLETE -> throw new IllegalStateException("Table is complete, cannot start or join a session.");
            default -> throw new IllegalStateException("Unhandled table status: " + status);
        }

        tableSession.setFoodVenue(foodVenue);
        tableSession.setTable(table);
        tableSessionRepository.save(tableSession);

        generateInitSessionResponseDto(tableSession, tableSession.getSessionHost());
        InitSessionResponseDto authResponse = generateInitSessionResponseDto(tableSession, tableSession.getSessionHost());

        log.info("[TableSession] Initialized entity successfully tableId={}...tableSessionId={}", table.getId(), tableSession.getId());
        return authResponse;
    }

    private TableSession verifyActiveTableSessionForAuthUser(User authUser) {
        log.debug("[AuthService] Verifying active table session.");
        return tableSessionRepository.findActiveSessionByUserId(authUser.getEmail()).orElse(null);
    }


    private TableSession initSession(Table table, User authUser) {
        log.debug("[TableSession] Initializing table session tableId={}...", table.getId());

        TableSession tableSession = TableSession.builder()
                .table(table)
                .foodVenue(table.getFoodVenue())
                .startTime(LocalDateTime.now())
                .build();
        Participant participant = participantService.create(authUser, tableSession);
        log.debug("[TableSession] Participant init table session. TableSessionID={}. Role={}...", tableSession.getId(), participant.getRole());

        tableSession.setSessionHost(participant);
        tableSession.getParticipants().add(participant);

        return tableSession;

    }

    private TableSession joinSession(UUID tableId, User authUser) {

        TableSession tableSession = tableSessionRepository.findTableSessionByTable_IdAndTableStatus(tableId, TableStatus.OCCUPIED)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME, tableId.toString()));

        Participant participant = participantService.create(authUser, tableSession);
        log.debug("[TableSession] Participant joined to session. TableSessionID={}. Role={}...", tableSession.getId(), participant.getRole());

        tableSession.getParticipants().add(participant);

        return tableSession;
    }

    private InitSessionResponseDto generateInitSessionResponseDto(TableSession tableSession, Participant participant) {

        String token = jwtService.generateToken(
                (participant.getUser() != null) ? participant.getUser().getEmail() : participant.getNickname(),
                tableSession.getFoodVenue().getId(),
                participant.getRole().name(),
                tableSession.getId(),
                participant.getId()
        );

        ParticipantResponseDto participantDto = participantMapper.toResponseDto(participant);

        return InitSessionResponseDto.builder()
                .tableNumber(tableSession.getTable().getNumber())
                .startTime(tableSession.getStartTime())
                .endTime(tableSession.getEndTime())
                .participants(tableSession.getParticipants().stream()
                        .map(participantMapper::toResponseDto)
                        .toList())
                .hostClient(participantDto)
                .token(token)
                .build();
    }

    @Override
    public List<TableSessionResponseDto> getAll() {
        return tableSessionRepository.findByFoodVenueId(tenantContext.getCurrentFoodVenue().getId()).stream()
                .map(tableSessionMapper::toDTO)
                .toList();
    }

    @Override
    public TableSessionResponseDto getById(UUID id) {
        TableSession tableSession = getEntityById(id);
        return tableSessionMapper.toDTO(tableSession);
    }

    @Override
    public TableSession getEntityById(UUID sessionId) {
        return tableSessionRepository.findById(sessionId)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME, sessionId.toString()));
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
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME));
        return tableSessionMapper.toDTO(tableSession);
    }

    @Override
    public TableSessionResponseDto update(TableSessionUpdateDto tableSessionUpdateDto, UUID id) {
        TableSession tableSession = getEntityById(id);

        if (tableSessionUpdateDto.getEndTime() != null) {
            tableSession.setEndTime(tableSessionUpdateDto.getEndTime());
        }

        if (tableSessionUpdateDto.getParticipantIds() != null) {
            List<Participant> participants = new ArrayList<>();

            for (UUID participantId : tableSessionUpdateDto.getParticipantIds()) {
                Participant participant = participantService.getEntityById(participantId);
                participants.add(participant);
            }
            tableSession.setParticipants(participants);
        }

        TableSession updatedTableSession = tableSessionRepository.save(tableSession);

        return tableSessionMapper.toDTO(updatedTableSession);
    }

    @Override
    public TableSessionResponseDto addClient(UUID sessionId, UUID clientId) {
        TableSession tableSession = getEntityById(sessionId);

        Participant participant = participantService.getEntityById(clientId);

        tableSession.getParticipants().add(participant);

        TableSession updatedTableSession = tableSessionRepository.save(tableSession);

        return tableSessionMapper.toDTO(updatedTableSession);
    }

    @Override
    public TableSessionResponseDto closeSession(UUID tableId) {
        return null;
    }

}
