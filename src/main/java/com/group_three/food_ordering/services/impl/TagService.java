package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.dtos.create.TagCreateDto;
import com.group_three.food_ordering.dtos.response.TagResponseDto;
import com.group_three.food_ordering.dtos.update.TagUpdateDto;
import com.group_three.food_ordering.services.interfaces.ITagService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TagService implements ITagService {


    @Override
    public TagResponseDto create(TagCreateDto tagCreateDto) {
        return null;
    }

    @Override
    public List<TagResponseDto> getAll() {
        return List.of();
    }

    @Override
    public TagResponseDto getById(Long id) {
        return null;
    }

    @Override
    public void delete(Long id) {

    }

    @Override
    public TagResponseDto update(TagUpdateDto tagUpdateDto) {
        return null;
    }
}
