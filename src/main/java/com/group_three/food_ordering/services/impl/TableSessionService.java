package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.context.TenantContext;
import com.group_three.food_ordering.dtos.create.LoginRequest;
import com.group_three.food_ordering.dtos.create.TableCreateDto;
import com.group_three.food_ordering.dtos.create.TableSessionCreateDto;
import com.group_three.food_ordering.dtos.response.AuthResponse;
import com.group_three.food_ordering.dtos.response.TableSessionResponseDto;
import com.group_three.food_ordering.dtos.update.TableSessionUpdateDto;
import com.group_three.food_ordering.enums.RoleType;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.exceptions.TableSessionNotFoundException;
import com.group_three.food_ordering.mappers.TableSessionMapper;
import com.group_three.food_ordering.models.*;
import com.group_three.food_ordering.repositories.IClientRepository;
import com.group_three.food_ordering.repositories.ITableSessionRepository;
import com.group_three.food_ordering.repositories.IUserRepository;
import com.group_three.food_ordering.security.JwtService;
import com.group_three.food_ordering.services.interfaces.IClientService;
import com.group_three.food_ordering.services.interfaces.ITableSessionService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
public class TableSessionService implements ITableSessionService {

    private final ITableSessionRepository tableSessionRepository;
    private final TableSessionMapper tableSessionMapper;
    private final TableService tableService;
    private final IClientService clientService;
    private final TenantContext tenantContext;

    private final IUserRepository userRepository;
    private final IClientRepository clientRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

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



    @Override
    public TableSessionResponseDto openSession(UUID tableSessionId, LoginRequest loginRequest) {

        Optional<User> userOpt = userRepository.findByEmail(loginRequest.getEmail());

        //User user = userRepository.findByEmail(loginRequest.getEmail()).orElse(null);
        TableSession tableSession = tableSessionRepository.findById(tableSessionId)
                .orElseThrow(() -> new EntityNotFoundException("Mesa no encontrada"));
        Table table = tableSession.getTable();
        FoodVenue foodVenue = tableSession.getFoodVenue();


        if (userOpt.isPresent()) {
            User user = userOpt.get();


            if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
                throw new BadCredentialsException("Usuario o contraseña incorrectos");
            }


            Client client = clientRepository.findByUser_Email(user.getEmail())
                    .orElseThrow(() -> new BadCredentialsException("Cliente no encontrado"));

            user.setRole(RoleType.ROLE_CLIENT);
            userRepository.save(user);
            clientRepository.save(client);

            tableSession.setHostClient(client);
            tableSession.setParticipants(new ArrayList<>(List.of(client)));

            List<Client> participants = tableSession.getParticipants();
            if (participants == null) {
                participants = new ArrayList<>();
            }
            if (!participants.contains(client)) {
                participants.add(client);
            }
            tableSession.setParticipants(participants);
            tableSessionRepository.save(tableSession);



            String token = jwtService.generateToken(user.getEmail(), foodVenue.getId(), user.getRole().name());

            return TableSessionResponseDto.builder()
                    .id(tableSession.getId())
                    .tableId(table.getId())
                    .tableNumber(table.getNumber())
                    .startTime(tableSession.getStartTime())
                    .endTime(tableSession.getEndTime())
                    .hostClientId(client.getId())
                    .participantsIds(tableSession.getParticipants().stream().map(Client::getId).toList())
                    .token(token)
                    .build();
        }
        else {
            return guestInit(table, foodVenue, tableSession);
        }
    }


    private TableSessionResponseDto guestInit(Table table, FoodVenue foodVenue, TableSession tableSession) {
        String guestEmail = "guest@guest.local";

        Client client = Client.builder()
                .id(UUID.randomUUID())
                .nickname("Guest")
                .build();
        String token = jwtService.generateToken(
                guestEmail,
                foodVenue.getId(),
                RoleType.ROLE_GUEST.name()
        );

        List<Client> participants = tableSession.getParticipants();
        if (participants == null) {
            participants = new ArrayList<>();
        }
        if (!participants.contains(client)) {
            participants.add(client);
        }
        /*
        tableSession.setParticipants(participants);
        tableSessionRepository.save(tableSession);
         */
        return TableSessionResponseDto.builder()
                .id(tableSession.getId())
                .tableId(table.getId())
                .tableNumber(table.getNumber())
                .startTime(tableSession.getStartTime())
                .endTime(null)
                .hostClientId(null)
                .participantsIds(participants.stream().map(Client::getId).toList())
                .token(token)
                .build();
    }

    @Override
    public TableSessionResponseDto joinSession(UUID tableId) {
        return null;
    }

    @Override
    public TableSessionResponseDto closeSession(UUID tableId) {
        return null;
    }
}
