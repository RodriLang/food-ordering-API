package com.group_three.food_ordering.mappers;

import com.group_three.food_ordering.dto.request.TableSessionRequestDto;
import com.group_three.food_ordering.dto.response.TableSessionResponseDto;
import com.group_three.food_ordering.models.Participant;
import com.group_three.food_ordering.models.TableSession;
import org.mapstruct.*;

import java.util.List;
import java.util.Objects;

@Mapper(componentModel = "spring")
public interface TableSessionMapper {

    @Mappings({
            @Mapping(source = "diningTable.number", target = "tableNumber"),
            @Mapping(source = "diningTable.status", target = "tableStatus"),
            @Mapping(source = "participants", target = "numberOfParticipants", qualifiedByName = "calculateNumberOfParticipants"),
            @Mapping(source = "diningTable.capacity", target = "tableCapacity"),
            @Mapping(target = "isHostClient",
                    expression = "java(tableSession.getSessionHost() != null && " +
                            "currentParticipantId != null && " +
                            "java.util.Objects.equals(tableSession.getSessionHost().getPublicId(), currentParticipantId))")
    })
    TableSessionResponseDto toDto(TableSession tableSession, @Context java.util.UUID currentParticipantId);

    TableSession toEntity(TableSessionRequestDto dto);

    @Named("calculateNumberOfParticipants")
    default Integer calculateNumberOfParticipants(List<Participant> participants){
        return participants.stream()
                .filter(participant -> Objects.isNull(participant.getLeftAt()))
                .toList()
                .size();
    }
}
