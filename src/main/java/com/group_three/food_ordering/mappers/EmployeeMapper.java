package com.group_three.food_ordering.mappers;

import com.group_three.food_ordering.dtos.create.EmployeeCreateDto;
import com.group_three.food_ordering.dtos.create.UserCreateDto;
import com.group_three.food_ordering.dtos.response.EmployeeResponseDto;
import com.group_three.food_ordering.dtos.response.UserResponseDto;
import com.group_three.food_ordering.dtos.update.EmployeeUpdateDto;
import com.group_three.food_ordering.dtos.update.UserUpdateDto;
import com.group_three.food_ordering.models.Employee;
import com.group_three.food_ordering.models.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "removedAt", ignore = true)
    User toUser(UserCreateDto dto);

    UserResponseDto toUserResponse(User user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDto(UserUpdateDto dto, @MappingTarget User user);
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    @Mapping(target = "foodVenue", ignore = true)
    Employee toEmployee(EmployeeCreateDto dto);

    @Mapping(source = "user", target = "user")
    @Mapping(source = "foodVenue.name", target = "foodVenueName")
    EmployeeResponseDto toResponseDto(Employee employee);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEmployeeFromDto(EmployeeUpdateDto dto, @MappingTarget Employee employee);
}
