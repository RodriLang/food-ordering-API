package com.group_three.food_ordering.mappers;

import com.group_three.food_ordering.dto.create.TableCreateDto;
import com.group_three.food_ordering.dto.response.TableResponseDto;
import com.group_three.food_ordering.models.Table;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TableMapper {

    TableResponseDto toDTO(Table table);

    Table toEntity(TableCreateDto tableDTO);
}
