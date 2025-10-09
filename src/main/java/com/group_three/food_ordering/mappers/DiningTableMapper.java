package com.group_three.food_ordering.mappers;

import com.group_three.food_ordering.dto.request.DiningTableRequestDto;
import com.group_three.food_ordering.dto.response.DiningTableResponseDto;
import com.group_three.food_ordering.models.DiningTable;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface DiningTableMapper {

    DiningTableResponseDto toDto(DiningTable diningTable);

    DiningTable toEntity(DiningTableRequestDto tableDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(@MappingTarget DiningTable productEntity, DiningTableRequestDto dto);
}
