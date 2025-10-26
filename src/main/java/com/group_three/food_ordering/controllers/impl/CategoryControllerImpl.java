package com.group_three.food_ordering.controllers.impl;

import com.group_three.food_ordering.controllers.CategoryController;
import com.group_three.food_ordering.dto.request.CategoryRequestDto;
import com.group_three.food_ordering.dto.response.CategoryResponseDto;
import com.group_three.food_ordering.services.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CategoryControllerImpl implements CategoryController {

    private final CategoryService categoryService;

    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'ROOT')")
    @Override
    public ResponseEntity<CategoryResponseDto> createCategory(
            @RequestBody @Valid CategoryRequestDto categoryRequestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.create(categoryRequestDto));
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'ROOT')")
    @Override
    public ResponseEntity<CategoryResponseDto> updateCategory(UUID id, CategoryRequestDto categoryRequestDto) {
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.update(id, categoryRequestDto));
    }


    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER','ROOT')")
    @Override
    public ResponseEntity<List<CategoryResponseDto>> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.getAll());
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STAFF', 'MANAGER','ROOT')")
    @Override
    public ResponseEntity<CategoryResponseDto> getCategoryById(UUID id) {
        return ResponseEntity.ok(categoryService.getById(id));
    }

    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN', 'ROOT')")
    @Override
    public ResponseEntity<Void> deleteCategory(UUID id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
