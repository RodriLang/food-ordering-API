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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    public ProductResponseDto update(UUID publicId, ProductRequestDto productRequestDto) {
        Product product = getEntityById(publicId);
        productMapper.updateEntity(product, productRequestDto);
        return applyProductRulesAndSave(product, productRequestDto);
    }

    @Override
    public ProductResponseDto getById(UUID publicId) {
        Product product = getEntityById(publicId);
        return productMapper.toDto(product);
    }

    @Override
    public Product getEntityById(UUID publicId) {
        return productRepository.findByPublicId(publicId)
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
    public Product getEntityByNameAndContext(String name) {
        UUID foodVenueId = tenantContext.determineCurrentFoodVenue().getPublicId();
        return productRepository.findByNameAndFoodVenue_PublicId(name, foodVenueId).stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(PRODUCT_ENTITY_NAME));

    }

    @Override
    public void delete(UUID publicId) {
        productRepository.deleteByPublicId(publicId);
    }


    @Override
    public Page<ProductResponseDto> getAll(Pageable pageable) {
        UUID foodVenueId = tenantContext.getCurrentFoodVenue().getPublicId();
        return productRepository.findAllByFoodVenue_PublicId(foodVenueId, pageable)
                .map(productMapper::toDto);
    }

    @Override
    public Page<ProductResponseDto> getAllAvailable(Pageable pageable) {
        UUID foodVenueId = tenantContext.getCurrentFoodVenue().getPublicId();
        return productRepository.findAllByFoodVenue_PublicIdAndAvailable(foodVenueId, true, pageable)
                .map(productMapper::toDto);
    }

    @Override
    public Page<ItemMenuResponseDto> getTopSellingProducts(int limit, int days, Pageable pageable) {
        LocalDateTime fromDate = LocalDateTime.now().minusDays(days);
        return productRepository.findTopSellingProducts(fromDate, PageRequest.of(0, limit))
                .map(productMapper::toItemMenuDto);
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
