package com.group_three.food_ordering.controllers.impl;

import com.group_three.food_ordering.controllers.TagController;
import com.group_three.food_ordering.dto.request.TagRequestDto;
import com.group_three.food_ordering.dto.response.TagResponseDto;
import com.group_three.food_ordering.services.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TagControllerImpl implements TagController {

    private final TagService tagService;

    @PreAuthorize("hasAnyRole('ADMIN', 'ROOT')")
    public ResponseEntity<TagResponseDto> createTag(
            @RequestBody @Valid TagRequestDto tagRequestDto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(tagService.create(tagRequestDto));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER', 'ROOT')")
    public ResponseEntity<List<TagResponseDto>> getAllTags() {
        return ResponseEntity.status(HttpStatus.OK).body(tagService.getAll());
    }
}