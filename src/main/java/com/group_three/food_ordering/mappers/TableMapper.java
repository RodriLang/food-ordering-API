package com.group_three.food_ordering.mappers;

import com.group_three.food_ordering.dtos.create.TableCreateDto;
import com.group_three.food_ordering.dtos.response.TableResponseDto;
import com.group_three.food_ordering.models.Table;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface TableMapper {

    @Mapping(source = "qrCode", target = "qrCode")
    TableResponseDto toDTO(Table table);

    Table toEntity(TableCreateDto tableDTO);
}
