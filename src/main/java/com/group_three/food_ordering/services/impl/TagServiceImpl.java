package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.dto.request.TagRequestDto;
import com.group_three.food_ordering.dto.response.TagResponseDto;
import com.group_three.food_ordering.mappers.TagMapper;
import com.group_three.food_ordering.models.Tag;
import com.group_three.food_ordering.repositories.TagRepository;
import com.group_three.food_ordering.services.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TagServiceImpl implements TagService {

    private final TagRepository tagRepository;
    private final TagMapper tagMapper;

    @Override
    public Tag createInternal(String tagRequestDto) {
        Tag tag = Tag.builder().label(tagRequestDto).build();
        log.info("[TagRepository] Save new tag: {}", tagRequestDto);
        return tagRepository.save(tag);
    }

    @Override
    public TagResponseDto create(TagRequestDto tagRequestDto) {
        Tag tag = tagMapper.toEntity(tagRequestDto);
        return tagMapper.toDto(tagRepository.save(tag));
    }

    @Override
    public Set<Tag> createIfNotExists(List<String> tagsRequestDto) {
        List<String> tagLabels = tagsRequestDto.stream()
                .toList();

        Set<Tag> tags = tagRepository.findAllByLabelIn(tagLabels);
        Set<String> tagsLabelsExisting = tags.stream()
                .map(Tag::getLabel)
                .collect(Collectors.toSet());

        Set<Tag> savedTags = tagsRequestDto.stream()
                .filter(tag -> !tagsLabelsExisting.contains(tag))
                .map(this::createInternal)
                .collect(Collectors.toSet());

        tags.addAll(savedTags);
        return tags;

    }

    @Override
    public List<TagResponseDto> getAll() {
        log.info("[TagRepository] FindAll tags");
        return tagRepository.findAll().stream()
                .map(tagMapper::toDto)
                .toList();
    }

    @Override
    public List<Tag> getAllInternal() {
        log.info("[TagRepository] FindAll tags");
        return tagRepository.findAll().stream()
                .toList();
    }

    @Override
    public Set<Tag> getAllByLabel(List<String> labels) {
        return tagRepository.findAllByLabelIn(labels);
    }


}