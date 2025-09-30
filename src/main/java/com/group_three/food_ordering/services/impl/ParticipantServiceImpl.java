package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.dto.response.ParticipantResponseDto;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.mappers.ParticipantMapper;
import com.group_three.food_ordering.models.Participant;
import com.group_three.food_ordering.models.TableSession;
import com.group_three.food_ordering.models.User;
import com.group_three.food_ordering.enums.RoleType;
import com.group_three.food_ordering.repositories.ParticipantRepository;
import com.group_three.food_ordering.services.ParticipantService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ParticipantServiceImpl implements ParticipantService {

    private final ParticipantRepository participantRepository;
    private final ParticipantMapper participantMapper;

    private static final String ENTITY_NAME = "Participant";


    @Override
    public Participant create(User user, TableSession tableSession) {

        Participant participant = Participant.builder()
                .tableSession(tableSession)
                .nickname((user != null) ? user.getName() : "Guest" + System.nanoTime())
                .role(user != null ? RoleType.ROLE_CLIENT : RoleType.ROLE_GUEST)
                .user(user)
                .build();

        participantRepository.save(participant);

        return participant;
    }

    @Override
    public Participant update(UUID participantIdUser, User user){
        Participant participant = getEntityById(participantIdUser);
        if (user != null) {
            participant.setUser(user);
        }
        participantRepository.save(participant);
        return participant;
    }

    @Override
    public List<ParticipantResponseDto> getAll() {
        return participantRepository.findAll().stream()
                .map(participantMapper::toResponseDto)
                .toList();
    }

    @Override
    public ParticipantResponseDto getById(UUID id) {
        Participant participant = participantRepository.findByIdAndUser_RemovedAtIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME, id.toString()));
        return participantMapper.toResponseDto(participant);
    }

    @Override
    public Participant getEntityById(UUID id) {
        return participantRepository.findByIdAndUser_RemovedAtIsNull(id)
                .orElseThrow(() -> new EntityNotFoundException(ENTITY_NAME, id.toString()));
    }
}