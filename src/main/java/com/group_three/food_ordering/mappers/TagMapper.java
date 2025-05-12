package com.group_three.food_ordering.mappers;

import com.group_three.food_ordering.dtos.create.TagCreateDto;
import com.group_three.food_ordering.dtos.response.TagResponseDto;
import com.group_three.food_ordering.models.Tag;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring")
public interface TagMapper {
    Tag toEntity(TagCreateDto tagDto);
    TagResponseDto toDTO(Tag tag);

}
