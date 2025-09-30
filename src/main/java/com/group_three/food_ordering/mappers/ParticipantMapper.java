package com.group_three.food_ordering.mappers;

import com.group_three.food_ordering.dto.create.ParticipantCreateDto;
import com.group_three.food_ordering.dto.response.ParticipantResponseDto;
import com.group_three.food_ordering.dto.update.UserUpdateDto;
import com.group_three.food_ordering.models.Participant;
import com.group_three.food_ordering.models.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ParticipantMapper {


    @Mapping(source = "user", target = "user")
    ParticipantResponseDto toResponseDto(Participant participant);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateParticipant(Participant participant, @MappingTarget User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    Participant toEntity(ParticipantCreateDto dto);
}
