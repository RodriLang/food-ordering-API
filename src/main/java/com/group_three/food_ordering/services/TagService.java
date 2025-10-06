package com.group_three.food_ordering.services;

import com.group_three.food_ordering.dto.request.TagRequestDto;
import com.group_three.food_ordering.dto.response.TagResponseDto;
import com.group_three.food_ordering.models.Tag;

import java.util.List;

public interface TagService {

    TagResponseDto create(TagRequestDto tagRequestDto);

    List<TagResponseDto> getAll();

    TagResponseDto getById(Long id);

    Tag getEntityById(Long id);

    void delete(Long id);

    TagResponseDto update(Long id, TagRequestDto tagRequestDto);
}
