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

    // Crear UserEntity desde UserCreateDto
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "removedAt", ignore = true)
    UserEntity toUser(UserCreateDto dto);

    // Convertir UserEntity a su DTO de respuesta
    UserResponseDto toUserResponse(UserEntity user);

    // Convertir Client a su DTO de respuesta
    @Mapping(source = "user", target = "user")
    ClientResponseDto toResponseDto(Client client);

    // Actualizar campos del Client si no son null
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateClientFromDto(ClientUpdateDto dto, @MappingTarget Client client);

    // Actualizar campos del UserEntity si no son null
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDto(UserUpdateDto dto, @MappingTarget UserEntity user);

    // Crear Client desde ClientCreateDto (sin asociar user aún)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    Client toEntity(ClientCreateDto dto);
}
