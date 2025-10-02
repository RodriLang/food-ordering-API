package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.context.TenantContext;
import com.group_three.food_ordering.dto.SessionInfo;
import com.group_three.food_ordering.dto.create.TableSessionCreateDto;
import com.group_three.food_ordering.dto.response.InitSessionResponseDto;
import com.group_three.food_ordering.dto.response.ParticipantResponseDto;
import com.group_three.food_ordering.dto.response.TableSessionResponseDto;
import com.group_three.food_ordering.dto.update.TableSessionUpdateDto;
import com.group_three.food_ordering.enums.RoleType;
import com.group_three.food_ordering.enums.TableStatus;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.mappers.ParticipantMapper;
import com.group_three.food_ordering.mappers.TableSessionMapper;
import com.group_three.food_ordering.models.*;
import com.group_three.food_ordering.repositories.TableSessionRepository;
import com.group_three.food_ordering.security.JwtService;
import com.group_three.food_ordering.services.AuthService;
import com.group_three.food_ordering.services.ParticipantService;
import com.group_three.food_ordering.services.TableService;
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
    private final TableService tableService;
    private final AuthService authService;
    private final ParticipantMapper participantMapper;

    private static final String ENTITY_NAME = "TableSession";

    @Transactional
    @Override
    public InitSessionResponseDto enter(TableSessionCreateDto tableSessionCreateDto) {
        log.debug("[TableSession] Processing table session entry for tableId={}", tableSessionCreateDto.getTableId());

        Table table = tableService.getEntityById(tableSessionCreateDto.getTableId());
        configureTenantContext(table.getFoodVenue());

        Optional<User> optionalUser = authService.getAuthUser();

        if (optionalUser.isPresent()) {
            User authUser = optionalUser.get();
            log.debug("[TableSession] Authenticated user with email={}", authUser.getEmail());

            // El usuario inicia sesión luego de haber accedido como invitado
            // Se asume que tiene un token con el rol de invitado
            Optional<RoleType> role = authService.getCurrentParticipant()
                    .map(Participant::getRole);
            log.debug("[TableSession] Participant role={}", role);
            if (role.isPresent() && role.get() == RoleType.ROLE_GUEST) {
                log.debug("[TableSession] Auth user {} came with guest token, migrating participant", authUser.getEmail());
                return handleGuestToClientMigration(authUser);
            }

            // El usuario autenticado tiene una sesión activa, se asigna esa sesión.
            // No puede iniciar otra hasta cerrar la actual
            TableSession activeSession = verifyActiveTableSessionForAuthUser(authUser);
            if (activeSession != null) {
                log.debug("[TableSession] Found active session {} for {}", activeSession.getId(), authUser.getEmail());
                return generateInitSessionResponseDto(activeSession, authService.getCurrentParticipant()
                        .orElseThrow(() -> new EntityNotFoundException("Participant")));
            }

            // El usuario autenticado no tiene sesión previa
            log.debug("[TableSession] No active session found. Creating new one for {}", authUser.getEmail());
            return handleNewTableSession(table, authUser);
        }

        // Invitado sin autenticar
        log.debug("[TableSession] Anonymous guest detected. Creating new guest session");
        return handleNewTableSession(table, null);
    }

    private InitSessionResponseDto handleGuestToClientMigration(User authUser) {
        log.debug("[TableSession] Getting table session of guest participant");
        TableSession guestSession = authService.getCurrentParticipant()
                .map(Participant::getTableSession)
                .orElseThrow(() -> new EntityNotFoundException("Active guest session not found"));

        log.debug("[TableSession] Migrating guest participant to client for session {}", guestSession.getId());

        Participant currentParticipant = authService.getCurrentParticipant()
                .orElseThrow(() -> new EntityNotFoundException("Participant"));

        participantService.update(currentParticipant.getId(), authUser);

        return generateInitSessionResponseDto(guestSession, currentParticipant);
    }

    private InitSessionResponseDto handleNewTableSession(Table table, User authUser) {
        log.debug("[TableSession] Handling new table session for tableId={}", table.getId());

        TableStatus status = table.getStatus();
        log.debug("[TableSession] Creating session for table={} with status={}", table.getId(), status);


        TableSession tableSession = switch (status) {
            case AVAILABLE -> initSession(table);
            case OCCUPIED ->
                    tableSessionRepository.findTableSessionByTable_IdAndTableStatus(table.getId(), TableStatus.OCCUPIED)
                            .orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME, table.getId().toString()));
            case OUT_OF_SERVICE -> throw new IllegalStateException(
                    "Table is out of service, cannot start or join a session.");
            case COMPLETE -> throw new IllegalStateException(
                    "Table is complete, cannot start or join a session.");
            default -> throw new IllegalStateException("Unhandled table status: " + status);
        };

        TableSession createdTableSession = tableSessionRepository.save(tableSession);
        log.debug("[TableSession] TableSession created with tableSessionId={}", createdTableSession.getId());

        Participant participant = participantService.create(authUser, tableSession);
        log.debug("[TableSession] Participant joined session {} with role={}", tableSession.getId(), participant.getRole());


        tableSession.getParticipants().add(participant);
        if (tableSession.getSessionHost() != null) {
            tableSession.setSessionHost(participant);
        }

        TableSession savedTableSession = tableSessionRepository.save(tableSession);

        InitSessionResponseDto response = generateInitSessionResponseDto(savedTableSession, participant);
        determineTableStatusPostSessionCreation(table, tableSession);

        log.info("[TableSession] Successfully initialized session. SessionId={}, User={}",
                tableSession.getId(), authUser != null ? authUser.getEmail() : "anonymous");

        return response;
    }

    private void determineTableStatusPostSessionCreation(Table table, TableSession tableSession) {

        if (table.getCapacity().equals(tableSession.getParticipants().size())) {
            tableService.updateStatus(TableStatus.COMPLETE, table.getId());
        } else {
            tableService.updateStatus(TableStatus.OCCUPIED, table.getId());
        }
    }

    private void configureTenantContext(FoodVenue foodVenue) {
        log.debug("[TableSession] Configuring tenant context for foodVenue={}", foodVenue.getId());
        tenantContext.setCurrentFoodVenueId(foodVenue.getId().toString());
    }

    private TableSession verifyActiveTableSessionForAuthUser(User authUser) {
        log.debug("[AuthService] Verifying active table session.");
        return tableSessionRepository.findActiveSessionByUserEmail(authUser.getEmail()).orElse(null);
    }


    private TableSession initSession(Table table) {
        log.debug("[TableSession] Initializing table session tableId={}", table.getId());

        return TableSession.builder()
                .table(table)
                .foodVenue(table.getFoodVenue())
                .startTime(LocalDateTime.now())
                .build();
    }

    private InitSessionResponseDto generateInitSessionResponseDto(TableSession tableSession, Participant participant) {

        String token = jwtService.generateAccessToken(SessionInfo.builder()
                .subject((participant.getUser() != null) ? participant.getUser().getEmail() : participant.getNickname())
                .foodVenueId(tableSession.getFoodVenue().getId())
                .participantId(participant.getId())
                .tableSessionId(tableSession.getId())
                .role(participant.getRole().name())
                .build());

        ParticipantResponseDto participantDto = participantMapper.toResponseDto(participant);

        return InitSessionResponseDto.builder()
                .tableNumber(tableSession.getTable().getNumber())
                .startTime(tableSession.getStartTime())
                .endTime(tableSession.getEndTime())
                .participants(tableSession.getParticipants().stream()
                        .map(participantMapper::toResponseDto)
                        .toList())
                .hostClient(participantDto)
                .accessToken(token)
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