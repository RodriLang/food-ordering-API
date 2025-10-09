package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.context.TenantContext;
import com.group_three.food_ordering.dto.SessionInfo;
import com.group_three.food_ordering.dto.request.TableSessionRequestDto;
import com.group_three.food_ordering.dto.response.AuthResponse;
import com.group_three.food_ordering.dto.response.ParticipantResponseDto;
import com.group_three.food_ordering.dto.response.TableSessionResponseDto;
import com.group_three.food_ordering.enums.PaymentStatus;
import com.group_three.food_ordering.enums.RoleType;
import com.group_three.food_ordering.enums.DiningTableStatus;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.exceptions.InvalidPaymentStatusException;
import com.group_three.food_ordering.mappers.ParticipantMapper;
import com.group_three.food_ordering.mappers.TableSessionMapper;
import com.group_three.food_ordering.models.*;
import com.group_three.food_ordering.repositories.TableSessionRepository;
import com.group_three.food_ordering.security.JwtService;
import com.group_three.food_ordering.services.AuthService;
import com.group_three.food_ordering.services.ParticipantService;
import com.group_three.food_ordering.services.DiningTableService;
import com.group_three.food_ordering.services.TableSessionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

import static com.group_three.food_ordering.utils.EntityName.TABLE_SESSION;
import static com.group_three.food_ordering.utils.EntityName.PARTICIPANT;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class TableSessionServiceImpl implements TableSessionService {

    private final TableSessionRepository tableSessionRepository;
    private final TableSessionMapper tableSessionMapper;
    private final ParticipantService participantService;
    private final TenantContext tenantContext;
    private final JwtService jwtService;
    private final DiningTableService diningTableService;
    private final AuthService authService;
    private final ParticipantMapper participantMapper;

    @Override
    public AuthResponse enter(TableSessionRequestDto tableSessionRequestDto) {
        log.debug("[TableSession] Processing table session entry for tableId={}", tableSessionRequestDto.getTableId());

        DiningTable diningTable = diningTableService.getEntityById(tableSessionRequestDto.getTableId());
        configureTenantContext(diningTable.getFoodVenue());

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
                log.debug("[TableSession] Found active session {} for {}", activeSession.getPublicId(), authUser.getEmail());
                return generateInitSessionResponseDto(activeSession, authService.getCurrentParticipant()
                        .orElseThrow(() -> new EntityNotFoundException("Participant")));
            }

            // El usuario autenticado no tiene sesión previa
            log.debug("[TableSession] No active session found. Creating new one for {}", authUser.getEmail());
            return handleNewTableSession(diningTable, authUser);
        }

        // Invitado sin autenticar
        log.debug("[TableSession] Anonymous guest detected. Creating new guest session");
        return handleNewTableSession(diningTable, null);
    }

    @Override
    public Page<TableSessionResponseDto> getAll(Pageable pageable) {
        UUID foodVenueId = tenantContext.getCurrentFoodVenueId();
        return tableSessionRepository.findByFoodVenuePublicId(foodVenueId, pageable)
                .map(tableSessionMapper::toDto);
    }

    @Override
    public TableSessionResponseDto getById(UUID id) {
        TableSession tableSession = getEntityById(id);
        return tableSessionMapper.toDto(tableSession);
    }

    @Override
    public TableSessionResponseDto getByCurrentParticipant() {
        Participant currentParticipant = authService.determineCurrentParticipant();
        TableSession tableSession = tableSessionRepository.findByParticipantsContains(List.of(currentParticipant))
                .orElseThrow(() -> new EntityNotFoundException(PARTICIPANT));
        return tableSessionMapper.toDto(tableSession);
    }

    @Override
    public TableSession getEntityById(UUID sessionId) {
        return tableSessionRepository.findByPublicId(sessionId)
                .orElseThrow(() -> new EntityNotFoundException(TABLE_SESSION, sessionId.toString()));
    }

    @Override
    public Page<TableSessionResponseDto> getByFoodVenueAndTable(UUID foodVenueId, Integer tableNumber, Pageable pageable) {
        return tableSessionRepository.findByFoodVenuePublicIdAndDiningTableNumber(foodVenueId, tableNumber, pageable)
                .map(tableSessionMapper::toDto);
    }

    @Override
    public Page<TableSessionResponseDto> getByContextAndTable(Integer tableNumber, Pageable pageable) {

        UUID foodVenueId = tenantContext.getCurrentFoodVenue().getPublicId();

        return tableSessionRepository.findByFoodVenuePublicIdAndDiningTableNumber(foodVenueId, tableNumber, pageable)
                .map(tableSessionMapper::toDto);
    }

    @Override
    public Page<TableSessionResponseDto> getByTableAndTimeRange(
            Integer tableNumber, LocalDateTime start, LocalDateTime end, Pageable pageable) {
        LocalDateTime effectiveEnd = (end == null) ? LocalDateTime.now() : end;
        UUID foodVenueId = tenantContext.getCurrentFoodVenueId();

        if (start.isAfter(effectiveEnd)) {
            throw new IllegalArgumentException("Start date must be before or equal to end date");
        }

        return tableSessionRepository
                .findByFoodVenuePublicIdAndDiningTableNumberAndEndTimeGreaterThanEqualAndStartTimeLessThanEqual(
                        foodVenueId, tableNumber, start, effectiveEnd, pageable)
                .map(tableSessionMapper::toDto);
    }

    @Override
    public Page<TableSessionResponseDto> getActiveSessions(Pageable pageable) {
        UUID foodVenueId = tenantContext.getCurrentFoodVenueId();

        return tableSessionRepository
                .findByFoodVenuePublicIdAndEndTimeIsNull(foodVenueId, pageable)
                .map(tableSessionMapper::toDto);
    }

    @Override
    public Page<TableSessionResponseDto> getByHostClient(UUID clientId, Pageable pageable) {
        UUID foodVenueId = tenantContext.getCurrentFoodVenueId();
        return tableSessionRepository.findByFoodVenuePublicIdAndSessionHostPublicId(
                foodVenueId, clientId, pageable).map(tableSessionMapper::toDto);
    }

    @Override
    public Page<TableSessionResponseDto> getByAuthUserHostClient(Pageable pageable) {

        UUID authClientId = authService.determineCurrentTableSession().getPublicId();
        UUID foodVenueId = tenantContext.getCurrentFoodVenueId();

        return tableSessionRepository.findByFoodVenuePublicIdAndSessionHostPublicId(
                foodVenueId, authClientId, pageable).map(tableSessionMapper::toDto);
    }

    @Override
    public Page<TableSessionResponseDto> getPastByParticipant(UUID clientId, Pageable pageable) {
        UUID foodVenueId = tenantContext.getCurrentFoodVenueId();
        return tableSessionRepository.findPastSessionsByParticipantIdAndDeletedFalse(
                foodVenueId, clientId, pageable).map(tableSessionMapper::toDto);
    }

    @Override
    public Page<TableSessionResponseDto> getPastByAuthUserParticipant(Pageable pageable) {

        UUID authClientId = authService.determineAuthUser().getPublicId();
        UUID foodVenueId = tenantContext.getCurrentFoodVenueId();

        return tableSessionRepository.findPastSessionsByParticipantIdAndDeletedFalse(
                foodVenueId, authClientId, pageable).map(tableSessionMapper::toDto);
    }

    @Override
    public TableSessionResponseDto getLatestByTable(UUID tableId) {
        TableSession tableSession = tableSessionRepository.findTopByFoodVenuePublicIdAndDiningTablePublicIdOrderByStartTimeDesc(
                        tenantContext.getCurrentFoodVenueId(), tableId)
                .orElseThrow(() -> new EntityNotFoundException(TABLE_SESSION));
        return tableSessionMapper.toDto(tableSession);
    }

    @Override
    public TableSessionResponseDto addClient(UUID sessionId, UUID clientId) {
        TableSession tableSession = getEntityById(sessionId);
        Participant participant = participantService.getEntityById(clientId);
        tableSession.getParticipants().add(participant);
        TableSession updatedTableSession = tableSessionRepository.save(tableSession);
        return tableSessionMapper.toDto(updatedTableSession);
    }

    @Override
    public TableSessionResponseDto closeCurrentSession() {

        Participant currentHost = authService.determineCurrentParticipant();
        TableSession currentTableSession = authService.determineCurrentTableSession();

        if (!currentTableSession.getSessionHost().getPublicId().equals(currentHost.getPublicId())) {
            throw new AccessDeniedException("Only the current host can end the session");
        }
        return closeSession(currentTableSession);
    }

    @Override
    public TableSessionResponseDto closeSessionById(UUID tableId) {

        TableSession tableSession = tableSessionRepository.findTableSessionByDiningTable_PublicIdAndDiningTableStatus(
                tableId, DiningTableStatus.IN_SESSION).orElseThrow(() -> new EntityNotFoundException(TABLE_SESSION));

        return closeSession(tableSession);
    }

    private TableSessionResponseDto closeSession(TableSession tableSession) {

        List<Order> orders = tableSession.getOrders();

        boolean completedPayments = orders.stream()
                .map(order -> order.getPayment().getStatus())
                .allMatch(paymentStatus -> paymentStatus.equals(PaymentStatus.COMPLETED));
        if (!completedPayments) {
            throw new InvalidPaymentStatusException("All payments must be paid to finish table session");
        }

        tableSession.setEndTime(LocalDateTime.now());
        diningTableService.updateStatus(DiningTableStatus.WAITING_RESET, tableSession.getDiningTable().getPublicId());
        tableSessionRepository.save(tableSession);

        return tableSessionMapper.toDto(tableSession);

    }

    private AuthResponse handleGuestToClientMigration(User authUser) {
        log.debug("[TableSession] Getting table session of guest participant");
        TableSession guestSession = authService.getCurrentParticipant()
                .map(Participant::getTableSession)
                .orElseThrow(() -> new EntityNotFoundException("Active guest session not found"));

        log.debug("[TableSession] Migrating guest participant to client for session {}", guestSession.getPublicId());

        Participant currentParticipant = authService.getCurrentParticipant()
                .orElseThrow(() -> new EntityNotFoundException(PARTICIPANT));

        participantService.update(currentParticipant.getPublicId(), authUser);

        return generateInitSessionResponseDto(guestSession, currentParticipant);
    }

    private AuthResponse handleNewTableSession(DiningTable diningTable, User authUser) {
        log.debug("[TableSession] Handling new table session for tableId={}", diningTable.getPublicId());

        DiningTableStatus status = diningTable.getStatus();
        log.debug("[TableSession] Creating session for table={} with status={}", diningTable.getPublicId(), status);


        TableSession tableSession = switch (status) {
            case AVAILABLE -> initSession(diningTable);
            case IN_SESSION ->
                    tableSessionRepository.findTableSessionByDiningTable_PublicIdAndDiningTableStatus(diningTable.getPublicId(), DiningTableStatus.IN_SESSION)
                            .orElseThrow(() -> new EntityNotFoundException(TABLE_SESSION, diningTable.getPublicId().toString()));
            case OUT_OF_SERVICE -> throw new IllegalStateException(
                    "Table is out of service, cannot start or join a session.");
            case COMPLETE -> throw new IllegalStateException(
                    "Table is complete, cannot start or join a session.");
            default -> throw new IllegalStateException("Unhandled table status: " + status);
        };


        //verificar que la session se guarda 2 veces porque se necesita id para crear el participante

        tableSession.setStartTime(LocalDateTime.now());
        tableSession.setPublicId(UUID.randomUUID());
        TableSession createdTableSession = tableSessionRepository.save(tableSession);
        log.debug("[TableSession] TableSession created with tableSessionId={}", createdTableSession.getPublicId());

        Participant participant = participantService.create(authUser, tableSession);
        log.debug("[TableSession] Participant joined session {} with role={}", tableSession.getPublicId(), participant.getRole());


        tableSession.getParticipants().add(participant);
        if (tableSession.getSessionHost() != null) {
            tableSession.setSessionHost(participant);
        }

        TableSession savedTableSession = tableSessionRepository.save(tableSession);

        AuthResponse response = generateInitSessionResponseDto(savedTableSession, participant);
        determineTableStatusPostSessionCreation(diningTable, tableSession);

        log.info("[TableSession] Successfully initialized session. SessionId={}, User={}",
                tableSession.getPublicId(), authUser != null ? authUser.getEmail() : "anonymous");

        return response;
    }

    private void determineTableStatusPostSessionCreation(DiningTable diningTable, TableSession tableSession) {

        if (diningTable.getCapacity().equals(tableSession.getParticipants().size())) {
            diningTableService.updateStatus(DiningTableStatus.COMPLETE, diningTable.getPublicId());
        } else {
            diningTableService.updateStatus(DiningTableStatus.IN_SESSION, diningTable.getPublicId());
        }
    }

    private void configureTenantContext(FoodVenue foodVenue) {
        log.debug("[TableSession] Configuring tenant context for foodVenue={}", foodVenue.getPublicId());
        tenantContext.setCurrentFoodVenueId(foodVenue.getPublicId().toString());
    }

    private TableSession verifyActiveTableSessionForAuthUser(User authUser) {
        log.debug("[AuthService] Verifying active table session.");
        return tableSessionRepository.findActiveSessionByUserEmailAndDeletedFalse(authUser.getEmail()).orElse(null);
    }


    private TableSession initSession(DiningTable diningTable) {
        log.debug("[TableSession] Initializing table session tableId={}", diningTable.getPublicId());

        return TableSession.builder()
                .diningTable(diningTable)
                .foodVenue(diningTable.getFoodVenue())
                .startTime(LocalDateTime.now())
                .build();
    }

    private AuthResponse generateInitSessionResponseDto(TableSession tableSession, Participant participant) {

        String token = jwtService.generateAccessToken(SessionInfo.builder()
                .subject((participant.getUser() != null) ? participant.getUser().getEmail() : participant.getNickname())
                .userId(participant.getUser().getPublicId())
                .foodVenueId(tableSession.getFoodVenue().getPublicId())
                .participantId(participant.getPublicId())
                .tableSessionId(tableSession.getPublicId())
                .role(participant.getRole().name())
                .build());

        ParticipantResponseDto participantDto = participantMapper.toResponseDto(participant);

        return AuthResponse.builder()
                .tableNumber(tableSession.getDiningTable().getNumber())
                .startTime(tableSession.getStartTime())
                .endTime(tableSession.getEndTime())
                .participants(tableSession.getParticipants().stream()
                        .map(participantMapper::toResponseDto)
                        .toList())
                .hostClient(participantDto)
                .accessToken(token)
                .build();
    }

}