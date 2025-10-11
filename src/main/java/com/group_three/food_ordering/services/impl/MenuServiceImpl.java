package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.context.RequestContext;
import com.group_three.food_ordering.dto.response.*;
import com.group_three.food_ordering.mappers.ProductMapper;
import com.group_three.food_ordering.models.Category;
import com.group_three.food_ordering.models.FoodVenue;
import com.group_three.food_ordering.models.Product;
import com.group_three.food_ordering.repositories.FoodVenueRepository;
import com.group_three.food_ordering.repositories.ProductRepository;
import com.group_three.food_ordering.services.CategoryService;
import com.group_three.food_ordering.services.MenuService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final CategoryService categoryService;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final RequestContext requestContext;
    private final FoodVenueRepository foodVenueRepository;

    @Override
    public MenuResponseDto getCurrentContextHierarchicalMenu(String category) {
        FoodVenue foodVenue = requestContext.requireFoodVenue();
        if (foodVenue == null) {
            throw new IllegalStateException("No tenant context available for the current request.");
        }
        return getHierarchicalMenuByFoodVenueId(foodVenue.getPublicId(), category);
    }

    @Override
    public MenuResponseDto getHierarchicalMenuByFoodVenueId(UUID foodVenueId, String category) {
        FoodVenue foodVenue = foodVenueRepository.findByPublicId(foodVenueId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid foodVenueId: " + foodVenueId));
        return generateHierarchicalMenu(foodVenue, category);
    }

    private MenuResponseDto generateHierarchicalMenu(FoodVenue foodVenue, String category) {

        List<Category> rootCategories = categoryService.findParentCategories(foodVenue.getPublicId());

        List<CategoryMenuResponseDto> categoriesDto;

        if (category == null) {
            categoriesDto = rootCategories.stream()
                    .map(root -> buildCategoryTree(root, foodVenue.getPublicId()))
                    .toList();
        } else {
            categoriesDto = rootCategories.stream()
                    .map(root -> buildCategoryTree(root, foodVenue.getPublicId()))
                    .map(root -> findLeafCategory(root, category))
                    .flatMap(Optional::stream)
                    .toList();
        }

        return MenuResponseDto.builder()
                .foodVenueName(foodVenue.getName())
                .foodVenueImageUrl(foodVenue.getVenueStyle().getLogoUrl())
                .menu(categoriesDto)
                .build();
    }

    /**
     * Construcción recursiva de categorías + productos
     */
    private CategoryMenuResponseDto buildCategoryTree(Category category, UUID foodVenueId) {
        // Productos de esta categoría
        List<Product> products = productRepository.findAllByFoodVenue_PublicIdAndAvailableAndCategoryPublicId(
                foodVenueId, true, category.getPublicId());

        List<ItemMenuResponseDto> productsDto = products.stream()
                .map(productMapper::toItemMenuDto)
                .toList();

        // Subcategorías (recursivo)
        List<CategoryMenuResponseDto> children = category.getChildrenCategories() != null
                ? category.getChildrenCategories().stream()
                .map(child -> buildCategoryTree(child, foodVenueId))
                .toList()
                : new ArrayList<>();

        return CategoryMenuResponseDto.builder()
                .category(category.getName())
                .products(productsDto)
                .subcategory(children)
                .build();
    }

    private Optional<CategoryMenuResponseDto> findLeafCategory(
            CategoryMenuResponseDto node, String category) {

        if ((node.getSubcategory() == null || node.getSubcategory().isEmpty())
                && node.getCategory().equalsIgnoreCase(category)) {
            return Optional.of(node);
        }

        if (node.getSubcategory() != null) {
            for (CategoryMenuResponseDto child : node.getSubcategory()) {
                Optional<CategoryMenuResponseDto> result = findLeafCategory(child, category);
                if (result.isPresent()) {
                    return result;
                }
            }
        }
        return Optional.empty();
    }
}
