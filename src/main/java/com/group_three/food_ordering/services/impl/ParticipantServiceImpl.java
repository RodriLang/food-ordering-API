package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.dto.response.LoginResponse;
import com.group_three.food_ordering.dto.response.ParticipantResponseDto;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.mappers.ParticipantMapper;
import com.group_three.food_ordering.models.Participant;
import com.group_three.food_ordering.models.TableSession;
import com.group_three.food_ordering.models.User;
import com.group_three.food_ordering.enums.RoleType;
import com.group_three.food_ordering.repositories.ParticipantRepository;
import com.group_three.food_ordering.services.AuthService;
import com.group_three.food_ordering.services.ParticipantService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ParticipantServiceImpl implements ParticipantService {

    private final ParticipantRepository participantRepository;
    private final ParticipantMapper participantMapper;
    private final AuthService authService;

    private static final String ENTITY_NAME = "Participant";

    @Override
    public Participant create(User user, TableSession tableSession) {

        Participant participant = Participant.builder()
                .publicId(UUID.randomUUID())
                .tableSession(tableSession)
                .nickname((user != null) ? user.getName() : "Guest" + System.nanoTime())
                .role(user != null ? RoleType.ROLE_CLIENT : RoleType.ROLE_GUEST)
                .user(user)
                .build();
        participantRepository.save(participant);
        log.debug("[ParticipantService] Participant created. Nickname={}. Role={}. User={}",
                participant.getNickname(), participant.getRole(), user != null ? user.getEmail() : null);

        return participant;
    }

    @Override
    public Participant update(UUID participantIdUser, User user) {
        Participant participant = getEntityById(participantIdUser);
        if (user != null) {
            participant.setUser(user);
            participant.setRole(RoleType.ROLE_CLIENT);
            participant.setNickname(user.getName());
        }
        participantRepository.save(participant);
        log.debug("[ParticipantService] Participant updated. Nickname={}. Role={}. User={}",
                participant.getNickname(), participant.getRole(), user != null ? user.getEmail() : null);
        return participant;
    }

    @Override
    public ParticipantResponseDto getById(UUID id) {
        Participant participant = participantRepository.findByPublicId(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME, id.toString()));
        return participantMapper.toResponseDto(participant);
    }

    @Override
    public Participant getEntityById(UUID id) {
        return participantRepository.findByPublicId(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME, id.toString()));
    }

    @Override
    public LoginResponse delegateHostingDuties(UUID participantId) {
        Participant hostDesigned = getEntityById(participantId);
        Participant currentHost = authService.determineCurrentParticipant();
        TableSession currentTableSession = authService.determineCurrentTableSession();

        if (!currentTableSession.getSessionHost().getPublicId().equals(currentHost.getPublicId())) {
            throw new AccessDeniedException("Only the current host can delegate hosting duties");
        }
        //implementar una forma de capturar este evento para avisar al nuevo hot
        currentTableSession.setSessionHost(hostDesigned);
        return null;
    }
}