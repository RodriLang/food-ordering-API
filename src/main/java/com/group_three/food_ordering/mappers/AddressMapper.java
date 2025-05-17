package com.group_three.food_ordering.mappers;

import com.group_three.food_ordering.dtos.create.AddressCreateDto;
import com.group_three.food_ordering.dtos.response.AddressResponseDto;
import com.group_three.food_ordering.models.Address;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    AddressResponseDto toDTO(Address address);
    Address toEntity(AddressCreateDto addressDTO);
}
