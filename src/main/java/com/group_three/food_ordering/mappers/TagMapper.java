package com.group_three.food_ordering.mappers;

import com.group_three.food_ordering.dto.request.TagRequestDto;
import com.group_three.food_ordering.dto.response.TagResponseDto;
import com.group_three.food_ordering.models.Tag;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface TagMapper {

    Tag toEntity(TagRequestDto tagDto);

    TagResponseDto toDto(Tag tag);

}
