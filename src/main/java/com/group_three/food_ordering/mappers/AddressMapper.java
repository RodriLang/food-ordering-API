package com.group_three.food_ordering.mappers;

import com.group_three.food_ordering.dto.create.AddressCreateDto;
import com.group_three.food_ordering.dto.response.AddressResponseDto;
import com.group_three.food_ordering.dto.update.AddressUpdateDto;
import com.group_three.food_ordering.models.Address;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    AddressResponseDto toDTO(Address address);
    Address toEntity(AddressCreateDto addressDTO);
    Address toEntity(AddressUpdateDto addressDTO);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntity(AddressUpdateDto dto, @MappingTarget Address entity);
}
