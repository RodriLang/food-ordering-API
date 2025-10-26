package com.group_three.food_ordering.mappers;

import com.group_three.food_ordering.dto.response.RoleEmploymentResponseDto;
import com.group_three.food_ordering.models.Employment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoleEmploymentMapper {

    @Mapping(target = "role" , source = "employment.role")
    @Mapping(target = "foodVenueName" , source = "employment.foodVenue.name")
    RoleEmploymentResponseDto toResponseDto(Employment employment);

}
