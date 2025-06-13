package com.group_three.food_ordering.services.interfaces;

import com.group_three.food_ordering.dtos.create.TagCreateDto;
import com.group_three.food_ordering.dtos.response.TagResponseDto;

import java.util.List;

public interface ITagService {
    TagResponseDto create(TagCreateDto tagCreateDto);
    List<TagResponseDto> getAll();
    TagResponseDto getById(Long id);
    void delete(Long id);
    TagResponseDto update(Long id, TagCreateDto tagCreateDto);
}
