package com.group_three.food_ordering.controllers;

import com.fasterxml.jackson.core.TreeCodec;
import com.group_three.food_ordering.configs.ApiPaths;
import com.group_three.food_ordering.dtos.create.CategoryCreateDto;
import com.group_three.food_ordering.dtos.response.CategoryResponseDto;
import com.group_three.food_ordering.services.interfaces.ICategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(ApiPaths.CATEGORY_BASE)
@RequiredArgsConstructor
public class CategoryController {

    private final ICategoryService categoryService;
    private final TreeCodec treeCodec;

    @PostMapping
    public ResponseEntity<CategoryResponseDto> createCategory(
            @RequestBody @Valid CategoryCreateDto categoryCreateDto) {
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.create(categoryCreateDto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> updateCategory(@PathVariable Long id,
                                                              @RequestBody @Valid CategoryCreateDto categoryCreateDto) {
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.update(id, categoryCreateDto));
        }

    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getAll() {
        return ResponseEntity.status(HttpStatus.OK).body(categoryService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDto> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
