package com.group_three.food_ordering.mappers;

import com.group_three.food_ordering.dtos.create.TableCreateDto;
import com.group_three.food_ordering.dtos.response.TableResponseDto;
import com.group_three.food_ordering.models.Table;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TableMapper {

    TableResponseDto toDTO(TableCreateDto tableCreateDto);

    TableResponseDto toDTO(Table table);

    Table toEntity(TableResponseDto tableDTO);

    Table toEntity(TableCreateDto tableDTO);
}

