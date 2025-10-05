package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.context.TenantContext;
import com.group_three.food_ordering.dto.request.ProductRequestDto;
import com.group_three.food_ordering.dto.response.ItemMenuResponseDto;
import com.group_three.food_ordering.dto.response.ProductResponseDto;
import com.group_three.food_ordering.exceptions.EntityNotFoundException;
import com.group_three.food_ordering.exceptions.InsufficientStockException;
import com.group_three.food_ordering.mappers.ProductMapper;
import com.group_three.food_ordering.models.Category;
import com.group_three.food_ordering.models.FoodVenue;
import com.group_three.food_ordering.models.Product;
import com.group_three.food_ordering.models.Tag;
import com.group_three.food_ordering.repositories.CategoryRepository;
import com.group_three.food_ordering.repositories.ProductRepository;
import com.group_three.food_ordering.repositories.TagRepository;
import com.group_three.food_ordering.services.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final TagRepository tagRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final TenantContext tenantContext;

    private static final String CATEGORY_ENTITY_NAME = "Category";
    private static final String PRODUCT_ENTITY_NAME = "Product";
    private static final String TAG_ENTITY_NAME = "Tag";

    @Override
    public ProductResponseDto create(ProductRequestDto productRequestDto) {
        Product product = productMapper.toEntity(productRequestDto);
        FoodVenue currentFoodVenue = tenantContext.getCurrentFoodVenue();
        product.setFoodVenue(currentFoodVenue);
        return applyProductRulesAndSave(product, productRequestDto);
    }

    @Override
    public ProductResponseDto update(Long id, ProductRequestDto productRequestDto) {
        Product product = getEntityById(id);
        productMapper.updateEntity(product, productRequestDto);
        return applyProductRulesAndSave(product, productRequestDto);
    }

    @Override
    public ProductResponseDto getById(Long id) {
        Product product = getEntityById(id);
        return productMapper.toDto(product);
    }

    @Override
    public Product getEntityById(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(PRODUCT_ENTITY_NAME));
    }

    @Override
    public ItemMenuResponseDto getByNameAndContext(String name) {
        UUID foodVenueId = tenantContext.determineCurrentFoodVenue().getPublicId();
        Product product = productRepository.findByNameAndFoodVenue_PublicId(name, foodVenueId).stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(PRODUCT_ENTITY_NAME));

        return productMapper.toItemMenuDto(product);
    }

    @Override
    public void delete(Long id) {
        productRepository.deleteById(id);
    }


    @Override
    public List<ProductResponseDto> getAll() {
        return productRepository.findAllByFoodVenue_PublicId(tenantContext.getCurrentFoodVenue().getPublicId()).stream()
                .map(productMapper::toDto)
                .toList();
    }

    @Override
    public List<ProductResponseDto> getAllAvailable() {
        return productRepository.findAllByFoodVenue_PublicIdAndAvailable(tenantContext.getCurrentFoodVenue().getPublicId(), true).stream()
                .map(productMapper::toDto)
                .toList();
    }

    private ProductResponseDto applyProductRulesAndSave(Product product, ProductRequestDto productRequestDto) {
        product.setPrice(productRequestDto.getPrice() != null ? productRequestDto.getPrice() : BigDecimal.ZERO);
        product.setStock(productRequestDto.getStock() != null ? productRequestDto.getStock() : 0);
        product.setAvailable(product.getStock() != null && product.getStock() > 0);
        product.setCategory(findCategory(productRequestDto.getCategoryId()));
        product.setTags(findTags(productRequestDto.getTagsId()));
        Product savedProduct = productRepository.save(product);
        return productMapper.toDto(savedProduct);
    }

    public void validateStock(Product product, Integer quantity) throws InsufficientStockException {
        if (product.getStock() < quantity) {
            throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
        }
    }

    public void incrementStockProduct(Product product, Integer quantity) {
        if (quantity != null && quantity > 0) {
            product.setStock(product.getStock() + quantity);
            product.setAvailable(product.getStock() + quantity > 0);
        }
    }

    public void decrementStockProduct(Product product, Integer quantity) {
        validateStock(product, quantity);
        product.setStock(product.getStock() - quantity);
        product.setAvailable(product.getStock() - quantity > 0);
    }

    private List<Tag> findTags(List<Long> tagsId) {
        if (tagsId != null && !tagsId.isEmpty()) {
            return tagsId.stream()
                    .map(tagId -> tagRepository.findById(tagId)
                            .orElseThrow(() -> new EntityNotFoundException(TAG_ENTITY_NAME)))
                    .toList();
        } else {
            return new ArrayList<>();
        }
    }

    private Category findCategory(Long categoryId) {
        if (categoryId != null) {
            return categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new EntityNotFoundException(CATEGORY_ENTITY_NAME));
        } else {
            return null;
        }
    }
}
