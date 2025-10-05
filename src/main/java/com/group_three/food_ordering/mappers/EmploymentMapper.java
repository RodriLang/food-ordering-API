package com.group_three.food_ordering.mappers;

import com.group_three.food_ordering.dto.request.EmploymentRequestDto;
import com.group_three.food_ordering.dto.response.EmploymentResponseDto;
import com.group_three.food_ordering.models.Employment;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = {UserMapper.class, FoodVenueMapper.class})
public interface EmploymentMapper {

    Employment toEntity(EmploymentRequestDto dto);

    @Mapping(target = "foodVenue", source = "foodVenue.name")
    EmploymentResponseDto toResponseDto(Employment employee);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void update(EmploymentRequestDto dto, @MappingTarget Employment employment);

}
