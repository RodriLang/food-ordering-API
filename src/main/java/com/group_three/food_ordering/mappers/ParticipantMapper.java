package com.group_three.food_ordering.mappers;

import com.group_three.food_ordering.dto.response.ParticipantResponseDto;
import com.group_three.food_ordering.models.Participant;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface ParticipantMapper {

    ParticipantResponseDto toResponseDto(Participant participant);

}
