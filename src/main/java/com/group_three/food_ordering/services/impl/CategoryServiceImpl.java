package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.context.RequestContext;
import com.group_three.food_ordering.dto.request.CategoryRequestDto;
import com.group_three.food_ordering.dto.response.CategoryResponseDto;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.mappers.CategoryMapper;
import com.group_three.food_ordering.models.Category;
import com.group_three.food_ordering.repositories.CategoryRepository;
import com.group_three.food_ordering.services.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import static com.group_three.food_ordering.utils.EntityName.CATEGORY;
import static com.group_three.food_ordering.utils.EntityName.FOOD_VENUE;

@Service
@RequiredArgsConstructor

public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final RequestContext requestContext;

    @Override
    public CategoryResponseDto create(CategoryRequestDto categoryRequestDto) {
        Category category = categoryMapper.toEntity(categoryRequestDto);
        if (categoryRequestDto.getParentCategoryId() != null) {
            Category parent = getEntityById(categoryRequestDto.getParentCategoryId());
            category.setParentCategory(parent);
        }
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public CategoryResponseDto update(UUID publicId, CategoryRequestDto categoryRequestDto) {
        Category category = getEntityById(categoryRequestDto.getParentCategoryId());
        category.setName(categoryRequestDto.getName());
        return categoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    public CategoryResponseDto getById(UUID publicId) {
        Category category = getEntityById(publicId);
        return categoryMapper.toDto(category);
    }

    @Override
    public Category getEntityById(UUID publicId) {
        UUID currentFoodVenueId = requestContext.foodVenueIdOpt()
                .orElseThrow(() -> new EntityNotFoundException(FOOD_VENUE));

        return categoryRepository.findByPublicIdAndFoodVenue_PublicId(publicId, currentFoodVenueId)
                .orElseThrow(() -> new EntityNotFoundException(CATEGORY));

    }

    @Override
    public void delete(UUID publicId) {
        Category category = getEntityById(publicId);
        category.setDeleted(Boolean.TRUE);
        categoryRepository.save(category);
    }

    @Override
    public List<CategoryResponseDto> getAll() {
        UUID currentFoodVenueId = requestContext.foodVenueIdOpt()
                .orElseThrow(() -> new EntityNotFoundException(FOOD_VENUE));

        List<Category> roots = categoryRepository.findByParentCategoryIsNullAndFoodVenue_PublicId(currentFoodVenueId);

        return roots.stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Override
    public List<CategoryResponseDto> getCategoriesByParentCategoryId(UUID publicId) {
        UUID currentFoodVenueId = requestContext.foodVenueIdOpt()
                .orElseThrow(() -> new EntityNotFoundException(FOOD_VENUE));

        List<Category> children = categoryRepository.findByParentCategoryPublicIdAndFoodVenue_PublicId(
                publicId, currentFoodVenueId);

        return children.stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Override
    public List<Category> findParentCategories(UUID foodVenuePublicId) {
        return categoryRepository.findAllByFoodVenue_PublicIdAndParentCategoryIsNull(foodVenuePublicId)
                .stream()
                .toList();
    }
}
