package com.group_three.food_ordering.services;

import com.group_three.food_ordering.dto.request.TagRequestDto;
import com.group_three.food_ordering.dto.response.TagResponseDto;
import com.group_three.food_ordering.models.Tag;

import java.util.List;
import java.util.Set;

public interface TagService {

    Tag createInternal(String tagRequestDto);

    TagResponseDto create(TagRequestDto tagRequestDto);

    Set<Tag> createIfNotExists(List<String>  tagsRequestDto);

    List<Tag> getAllInternal();

    List<TagResponseDto> getAll();

    Set<Tag> getAllByLabel(List<String> labels);

}
