package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.context.TenantContext;
import com.group_three.food_ordering.dto.response.*;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.mappers.ProductMapper;
import com.group_three.food_ordering.models.Category;
import com.group_three.food_ordering.models.FoodVenue;
import com.group_three.food_ordering.models.Product;
import com.group_three.food_ordering.repositories.FoodVenueRepository;
import com.group_three.food_ordering.repositories.ProductRepository;
import com.group_three.food_ordering.services.CategoryService;
import com.group_three.food_ordering.services.MenuService;
import com.group_three.food_ordering.utils.EntityName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MenuServiceImpl implements MenuService {

    private final CategoryService categoryService;
    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final TenantContext tenantContext;
    private final FoodVenueRepository foodVenueRepository;

    @Override
    public MenuResponseDto getCurrentContextHierarchicalMenu(String category) {
        FoodVenue foodVenue = tenantContext.requireFoodVenue();
        return getHierarchicalMenuByFoodVenueId(foodVenue.getPublicId(), category);
    }

    @Override
    public MenuResponseDto getHierarchicalMenuByFoodVenueId(UUID foodVenueId, String category) {
        FoodVenue foodVenue = foodVenueRepository.findByPublicIdAndDeletedFalse(foodVenueId)
                .orElseThrow(()-> new EntityNotFoundException(EntityName.FOOD_VENUE));
        return getHierarchicalMenuByFoodVenueId(foodVenue, category);
    }

    private MenuResponseDto getHierarchicalMenuByFoodVenueId(FoodVenue foodVenue, String categoryName) {

        // 1. Traer TODAS las categorías del local (1 sola consulta)
        log.debug("[CategoryService] Calling findAllByFoodVenue for venue {}", foodVenue.getPublicId());
        List<Category> allCategories = categoryService.findAllByFoodVenue(foodVenue.getPublicId());

        // 2. Traer TODOS los productos disponibles del local (1 sola consulta)
        log.debug("[ProductRepository] Calling findAllByFoodVenue (available) for venue {}", foodVenue.getPublicId());
        List<Product> allProducts = productRepository.findAllByFoodVenue_PublicIdAndAvailableAndDeletedFalse(
                foodVenue.getPublicId(), Boolean.TRUE);

        // --- PASO 2: PROCESAR EN MEMORIA (CERO QUERIES) ---

        // 3. Agrupar productos por ID de categoría para búsqueda O(1)
        Map<Long, List<Product>> productsByCategoryId = allProducts.stream()
                .collect(Collectors.groupingBy(product -> product.getCategory().getId()));

        // 4. Agrupar categorías hijas por ID de padre para búsqueda O(1)
        Map<Long, List<Category>> childrenByParentId = allCategories.stream()
                .filter(cat -> cat.getParentCategory() != null)
                .collect(Collectors.groupingBy(cat -> cat.getParentCategory().getId()));


        // --- PASO 3: CONSTRUIR ÁRBOL EN MEMORIA ---

        // 5. Construir el árbol recursivamente usando los Mapas (no consulta la BD)
        List<CategoryMenuResponseDto> categoriesDto = allCategories.stream()
                .filter(cat -> cat.getParentCategory() == null) // Empezar por las raíces
                .map(root -> buildCategoryTreeInMemory(root, childrenByParentId, productsByCategoryId))
                .toList();

        // 6. (Opcional) Filtrar por 'categoryName' si se proveyó
        if (categoryName != null) {
            categoriesDto = categoriesDto.stream()
                    .map(root -> findLeafCategory(root, categoryName))
                    .flatMap(Optional::stream)
                    .toList();
        }

        return MenuResponseDto.builder()
                .foodVenueName(foodVenue.getName())
                .foodVenueImageUrl(foodVenue.getVenueStyle().getLogoUrl())
                .menu(categoriesDto)
                .build();
    }

    private CategoryMenuResponseDto buildCategoryTreeInMemory(
            Category currentCategory,
            Map<Long, List<Category>> childrenByParentId,
            Map<Long, List<Product>> productsByCategoryId) {

        // 1. Buscar productos de esta categoría EN EL MAPA
        List<Product> products = productsByCategoryId.getOrDefault(currentCategory.getId(), Collections.emptyList());
        List<ItemMenuResponseDto> productsDto = products.stream()
                .map(productMapper::toItemMenuDto)
                .toList();

        // 2. Buscar subcategorías EN EL MAPA
        List<Category> children = childrenByParentId.getOrDefault(currentCategory.getId(), Collections.emptyList());

        // 3. Llamada recursiva para las hijas
        List<CategoryMenuResponseDto> childrenDto = children.stream()
                .map(child -> buildCategoryTreeInMemory(child, childrenByParentId, productsByCategoryId))
                .toList();

        return CategoryMenuResponseDto.builder()
                .category(currentCategory.getName())
                .products(productsDto)
                .subcategory(childrenDto)
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