package com.group_three.food_ordering.services;

import com.group_three.food_ordering.dto.create.TagCreateDto;
import com.group_three.food_ordering.dto.response.TagResponseDto;

import java.util.List;

public interface TagService {
    TagResponseDto create(TagCreateDto tagCreateDto);
    List<TagResponseDto> getAll();
    TagResponseDto getById(Long id);
    void delete(Long id);
    TagResponseDto update(Long id, TagCreateDto tagCreateDto);
}
