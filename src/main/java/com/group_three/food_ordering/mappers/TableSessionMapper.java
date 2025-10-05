package com.group_three.food_ordering.mappers;

import com.group_three.food_ordering.dto.request.TableSessionRequestDto;
import com.group_three.food_ordering.dto.response.TableSessionResponseDto;
import com.group_three.food_ordering.models.TableSession;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;

@Mapper(componentModel = "spring", uses = {ParticipantMapper.class})
public interface TableSessionMapper {

    @Mappings({
            @Mapping(source = "diningTable.number", target = "tableNumber"),
            @Mapping(source = "sessionHost", target = "hostClient"),
    })
    TableSessionResponseDto toDTO(TableSession tableSession);

    TableSession toEntity(TableSessionRequestDto dto);
}
