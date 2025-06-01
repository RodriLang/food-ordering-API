package com.group_three.food_ordering.mappers;

import com.group_three.food_ordering.dtos.create.ClientCreateDto;
import com.group_three.food_ordering.dtos.create.UserCreateDto;
import com.group_three.food_ordering.dtos.response.ClientResponseDto;
import com.group_three.food_ordering.dtos.response.UserResponseDto;
import com.group_three.food_ordering.dtos.update.ClientUpdateDto;
import com.group_three.food_ordering.dtos.update.UserUpdateDto;
import com.group_three.food_ordering.models.Client;
import com.group_three.food_ordering.models.UserEntity;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClientMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "removedAt", ignore = true)
    UserEntity toUser(UserCreateDto dto);

    UserResponseDto toUserResponse(UserEntity userEntity);

    @Mapping(source = "userEntity", target = "user")
    ClientResponseDto toResponseDto(Client client);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateClientFromDto(ClientUpdateDto dto, @MappingTarget Client client);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDto(UserUpdateDto dto, @MappingTarget UserEntity userEntity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userEntity", ignore = true)
    Client toEntity(ClientCreateDto dto);
}
