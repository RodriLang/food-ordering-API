package com.group_three.food_ordering.mappers;

import com.group_three.food_ordering.dto.request.AddressRequestDto;
import com.group_three.food_ordering.dto.response.AddressResponseDto;
import com.group_three.food_ordering.models.Address;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    AddressResponseDto toDto(Address address);

    Address toEntity(AddressRequestDto addressDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(AddressRequestDto dto, @MappingTarget Address entity);

}
