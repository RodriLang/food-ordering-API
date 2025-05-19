package com.group_three.food_ordering.mappers;

import com.group_three.food_ordering.dtos.create.TableSessionCreateDto;
import com.group_three.food_ordering.dtos.response.TableSessionResponseDto;
import com.group_three.food_ordering.models.TableSession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring")
public interface TableSessionMapper {

    @Mappings({
            @Mapping(source = "table.id", target = "tableId"),
            @Mapping(source = "table.number", target = "tableNumber"),
            @Mapping(source = "hostClient.id", target = "hostClientId"),
            @Mapping(target = "participantsIds", expression = "java(tableSession.getParticipants().stream().map(p -> p.getId()).toList())")
    })
    TableSessionResponseDto toDTO(TableSession tableSession);

    TableSession toEntity(TableSessionCreateDto dto);
}
