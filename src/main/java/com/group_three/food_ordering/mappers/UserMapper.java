package com.group_three.food_ordering.mappers;

import com.group_three.food_ordering.dto.create.UserCreateDto;
import com.group_three.food_ordering.dto.update.UserUpdateDto;
import com.group_three.food_ordering.dto.response.UserResponseDto;
import com.group_three.food_ordering.models.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @Mapping(target = "address", source = "address")
    User toEntity(UserCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(UserUpdateDto dto, @MappingTarget User userEntity);

    UserResponseDto toResponseDto(User userEntity);
}
