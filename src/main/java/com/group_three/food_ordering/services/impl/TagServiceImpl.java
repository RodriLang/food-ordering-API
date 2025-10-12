package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.dto.request.TagRequestDto;
import com.group_three.food_ordering.dto.response.TagResponseDto;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.mappers.TagMapper;
import com.group_three.food_ordering.models.Tag;
import com.group_three.food_ordering.repositories.TagRepository;
import com.group_three.food_ordering.services.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.group_three.food_ordering.utils.EntityName.TAG;

@Slf4j
@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    @Override
    public TagResponseDto create(TagRequestDto tagRequestDto) {
        Tag tag = tagMapper.toEntity(tagRequestDto);
        log.info("[TagRepository] Save new tag: {}", tagRequestDto.getLabel());
        return tagMapper.toDto(tagRepository.save(tag));
    }

    @Override
    public List<TagResponseDto> getAll() {
        log.info("[TagRepository] FindAll tags");
        return tagRepository.findAll().stream()
                .map(tagMapper::toDto)
                .toList();
    }

    @Override
    public TagResponseDto getById(Long id) {
        Tag tag = getEntityById(id);
        return tagMapper.toDto(tag);
    }

    @Override
    public Tag getEntityById(Long id) {
        log.info("[TagRepository] Find tag by id: {}", id);
        return tagRepository.findById(id).orElseThrow(() -> new EntityNotFoundException(TAG));
    }

    @Override
    public void delete(Long id) {
        log.info("[TagRepository] Delete tag by id: {}", id);
        tagRepository.deleteById(id);
    }

    @Override
    public TagResponseDto update(Long id, TagRequestDto tagRequestDto) {
        Tag tag = getEntityById(id);
        tag.setLabel(tagRequestDto.getLabel());
        log.info("[TagRepository] Update tag by id: {}", id);
        return tagMapper.toDto(tagRepository.save(tag));
    }
}