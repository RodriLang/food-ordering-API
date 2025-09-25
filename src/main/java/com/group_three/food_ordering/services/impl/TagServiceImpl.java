package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.dto.create.TagCreateDto;
import com.group_three.food_ordering.dto.response.TagResponseDto;
import com.group_three.food_ordering.mappers.TagMapper;
import com.group_three.food_ordering.models.Tag;
import com.group_three.food_ordering.repositories.TagRepository;
import com.group_three.food_ordering.services.TagService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    @Override
    public TagResponseDto create(TagCreateDto tagCreateDto) {
        Tag tag = tagMapper.toEntity(tagCreateDto);

        return tagMapper.toDTO(tagRepository.save(tag));
    }

    @Override
    public List<TagResponseDto> getAll() {
        return tagRepository.findAll().stream()
                .map(tagMapper::toDTO)
                .toList();
    }

    @Override
    public TagResponseDto getById(Long id) {
       Tag tag = tagRepository.findById(id).orElseThrow(NoSuchElementException::new);
       return tagMapper.toDTO(tag);
    }

    @Override
    public void delete(Long id) {
        tagRepository.deleteById(id);
    }

    @Override
    public TagResponseDto update(Long id, TagCreateDto tagCreateDto) {
        Tag tag = tagRepository.findById(id).orElseThrow(NoSuchElementException::new);
        tag.setLabel(tagCreateDto.getLabel());
        return tagMapper.toDTO(tagRepository.save(tag));
    }
}