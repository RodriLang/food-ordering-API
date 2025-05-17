package com.group_three.food_ordering.controllers;

import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dtos.create.CategoryCreateDto;
import com.group_three.food_ordering.dtos.create.TagCreateDto;
import com.group_three.food_ordering.dtos.response.TagResponseDto;
import com.group_three.food_ordering.dtos.update.TagUpdateDto;
import com.group_three.food_ordering.services.interfaces.ITagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.TAG_BASE)
@RequiredArgsConstructor
public class TagController {

    private final ITagService tagService;


    @PostMapping
    public ResponseEntity<TagResponseDto> createTag(
            @RequestBody @Valid TagCreateDto tagCreateDto) {
        return ResponseEntity.status(HttpStatus.OK).body(tagService.create(tagCreateDto));
    }
    @GetMapping
    public ResponseEntity<List<TagResponseDto>> getAllTags() {
        return ResponseEntity.status(HttpStatus.OK).body(tagService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<TagResponseDto> getTagById(@PathVariable Long id) {
        return ResponseEntity.status(HttpStatus.OK).body(tagService.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<TagResponseDto> deleteTag(@PathVariable Long id) {
        tagService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}")
    public ResponseEntity<TagResponseDto> updateTag(@PathVariable Long id,
                                                    @RequestBody @Valid TagCreateDto tagCreateDto)
    {
        return ResponseEntity.status(HttpStatus.OK).body(tagService.update(id, tagCreateDto));
    }
}
