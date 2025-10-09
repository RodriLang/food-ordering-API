package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.context.TenantContext;
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

@Service
@RequiredArgsConstructor

public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;
    private final TenantContext tenantContext;

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
        UUID currentFoodVenueId = tenantContext.getCurrentFoodVenueId();
        return categoryRepository.findByPublicIdAndFoodVenue_PublicId(publicId, currentFoodVenueId)
                .orElseThrow(() -> new EntityNotFoundException(CATEGORY));

    }

    @Override
    public void delete(UUID publicId) {
        UUID currentFoodVenueId = tenantContext.getCurrentFoodVenueId();
        categoryRepository.deleteByPublicIdAndFoodVenue_PublicId(publicId, currentFoodVenueId);
    }

    @Override
    public List<CategoryResponseDto> getAll() {
        UUID currentFoodVenueId = tenantContext.getCurrentFoodVenueId();
        List<Category> roots = categoryRepository.findByParentCategoryIsNullAndFoodVenue_PublicId(currentFoodVenueId);

        return roots.stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Override
    public List<CategoryResponseDto> getCategoriesByParentCategoryId(UUID publicId) {
        UUID currentFoodVenueId = tenantContext.getCurrentFoodVenueId();
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
