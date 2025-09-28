package com.group_three.food_ordering.services;

import com.group_three.food_ordering.dto.update.ParticipantPatchDto;
import com.group_three.food_ordering.dto.update.ParticipantUpdateDto;
import com.group_three.food_ordering.dto.response.ParticipantResponseDto;
import com.group_three.food_ordering.models.Participant;
import com.group_three.food_ordering.models.TableSession;
import com.group_three.food_ordering.models.User;

import java.util.List;
import java.util.UUID;

public interface ParticipantService {

    Participant create(User user, TableSession tableSession);

    List<ParticipantResponseDto> getAll();

    ParticipantResponseDto getById(UUID id);

    void delete(UUID id);

    ParticipantResponseDto update(UUID id, ParticipantUpdateDto participantUpdateDto);

    Participant getEntityById(UUID id);

    ParticipantResponseDto replace(UUID id, ParticipantUpdateDto dto);

    ParticipantResponseDto partialUpdate(UUID id, ParticipantPatchDto dto);

}
