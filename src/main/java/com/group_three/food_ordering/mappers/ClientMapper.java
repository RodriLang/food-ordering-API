package com.group_three.food_ordering.mappers;

import com.group_three.food_ordering.dtos.create.ClientCreateDto;
import com.group_three.food_ordering.dtos.create.UserCreateDto;
import com.group_three.food_ordering.dtos.response.ClientResponseDto;
import com.group_three.food_ordering.dtos.response.UserResponseDto;
import com.group_three.food_ordering.dtos.update.ClientUpdateDto;
import com.group_three.food_ordering.dtos.update.UserUpdateDto;
import com.group_three.food_ordering.models.Client;
import com.group_three.food_ordering.models.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClientMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "removedAt", ignore = true)
    User toUser(UserCreateDto dto);

    UserResponseDto toUserResponse(User user);

    @Mapping(source = "user", target = "user")
    ClientResponseDto toResponseDto(Client client);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateClientFromDto(ClientUpdateDto dto, @MappingTarget Client client);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDto(UserUpdateDto dto, @MappingTarget User user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    Client toEntity(ClientCreateDto dto);
}
