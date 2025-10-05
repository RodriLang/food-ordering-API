package com.group_three.food_ordering.mappers;

import com.group_three.food_ordering.dto.request.UserRequestDto;
import com.group_three.food_ordering.dto.response.UserResponseDto;
import com.group_three.food_ordering.models.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", uses = AddressMapper.class)
public interface UserMapper {

    User toEntity(UserRequestDto dto);

    UserResponseDto toResponseDto(User userEntity);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(com.group_three.food_ordering.dto.request.UserRequestDto dto, @MappingTarget User userEntity);
}
