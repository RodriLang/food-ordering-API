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
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.group_three.food_ordering.utils.EntityName.PRODUCT;
import static com.group_three.food_ordering.utils.EntityName.TAG;
import static com.group_three.food_ordering.utils.EntityName.CATEGORY;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final TagRepository tagRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final TenantContext tenantContext;

    @Override
    public ProductResponseDto create(ProductRequestDto productRequestDto) {
        Product product = productMapper.toEntity(productRequestDto);
        FoodVenue currentFoodVenue = tenantContext.requireFoodVenue();
        product.setFoodVenue(currentFoodVenue);
        product.setPublicId(UUID.randomUUID());
        log.debug("[ProductService] Applying rules and saving new product for venue {}", currentFoodVenue.getPublicId());
        return applyProductRulesAndSave(product, productRequestDto);
    }

    @Override
    public ProductResponseDto update(UUID publicId, ProductRequestDto productRequestDto) {
        Product product = getEntityById(publicId);
        productMapper.updateEntity(product, productRequestDto);
        log.debug("[ProductService] Applying rules and saving update for product {}", publicId);
        return applyProductRulesAndSave(product, productRequestDto);
    }

    @Override
    public ProductResponseDto getById(UUID publicId) {
        Product product = getEntityById(publicId);
        return productMapper.toDto(product);
    }

    @Override
    public Product getEntityById(UUID publicId) {
        log.debug("[ProductRepository] Calling findByPublicId for product publicId={}", publicId);
        return productRepository.findByPublicIdAndDeletedFalse(publicId)
                .orElseThrow(() -> new EntityNotFoundException(PRODUCT));
    }

    @Override
    public ItemMenuResponseDto getByNameAndContext(String name) {
        UUID foodVenueId = tenantContext.getFoodVenueId();
        log.debug("[ProductRepository] Calling findByName for ItemMenu for name={} and venueId={}", name, foodVenueId);
        Product product = productRepository.findByNameAndFoodVenue_PublicIdAndDeletedFalse(name, foodVenueId).stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(PRODUCT));

        return productMapper.toItemMenuDto(product);
    }

    @Override
    public Product getEntityByNameAndContext(String name) {
        UUID foodVenueId = tenantContext.getFoodVenueId();
        log.debug("[ProductRepository] Calling findByNameAndFoodVenue_PublicId for name={} and venueId={}", name, foodVenueId);
        return productRepository.findByNameAndFoodVenue_PublicIdAndDeletedFalse(name, foodVenueId).stream()
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException(PRODUCT));

    }

    @Override
    public void delete(UUID publicId) {
        Product product = getEntityById(publicId);
        product.setDeleted(Boolean.TRUE);
        log.debug("[ProductRepository] Calling save to soft delete product {}", publicId);
        productRepository.save(product);
    }


    @Override
    public Page<ProductResponseDto> getAll(Pageable pageable) {
        UUID foodVenueId = tenantContext.getFoodVenueId();
        log.debug("[ProductRepository] Calling findAllByFoodVenue_PublicId for venueId={}", foodVenueId);
        return productRepository.findAllByFoodVenue_PublicIdAndDeletedFalse(foodVenueId, pageable)
                .map(productMapper::toDto);
    }

    @Override
    public Page<ProductResponseDto> getAllAvailable(Pageable pageable) {
        UUID foodVenueId = tenantContext.getFoodVenueId();
        log.debug("[ProductRepository] Calling findAllByFoodVenue_PublicIdAndAvailable for venueId={}", foodVenueId);
        return productRepository.findAllByFoodVenue_PublicIdAndAvailableAndDeletedFalse(foodVenueId, true, pageable)
                .map(productMapper::toDto);
    }

    @Override
    public Page<ItemMenuResponseDto> getTopSellingProducts(int limit, int days, Pageable pageable) {
        Instant fromDate = Instant.now().minus(Duration.ofDays(days));
        log.debug("[ProductRepository] Calling findTopSellingProducts from date {} with limit {}", fromDate, limit);
        return productRepository.findTopSellingProducts(fromDate, PageRequest.of(0, limit))
                .map(productMapper::toItemMenuDto);
    }

    private ProductResponseDto applyProductRulesAndSave(Product product, ProductRequestDto productRequestDto) {
        product.setPrice(productRequestDto.getPrice() != null ? productRequestDto.getPrice() : BigDecimal.ZERO);
        product.setStock(productRequestDto.getStock() != null ? productRequestDto.getStock() : 0);
        product.setAvailable(product.getStock() != null && product.getStock() > 0);
        product.setCategory(findCategory(productRequestDto.getCategoryId()));
        product.setTags(findTags(productRequestDto.getTagsId()));
        log.debug("[ProductRepository] Calling save for product {}", product.getPublicId());
        Product savedProduct = productRepository.save(product);
        return productMapper.toDto(savedProduct);
    }

    public void validateStock(Product product, Integer quantity) throws InsufficientStockException {
        if (product.getStock() < quantity) {
            log.warn("[ProductService] Insufficient stock validation failed for product {} ({} < {})",
                    product.getPublicId(), product.getStock(), quantity);

            throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
        }
        log.debug("[ProductService] Stock validation successful for product {} ({} >= {})",
                product.getPublicId(), product.getStock(), quantity);
    }

    public void incrementStockProduct(Product product, Integer quantity) {
        if (quantity != null && quantity > 0) {
            log.debug("[ProductService] Incrementing stock for product {} by {}", product.getPublicId(), quantity);
            product.setStock(product.getStock() + quantity);
            product.setAvailable(product.getStock() + quantity > 0);
        }
    }

    public void decrementStockProduct(Product product, Integer quantity) {
        validateStock(product, quantity);
        log.debug("[ProductService] Decrementing stock for product {} by {}", product.getPublicId(), quantity);
        product.setStock(product.getStock() - quantity);
        product.setAvailable(product.getStock() - quantity > 0);
    }

    private List<Tag> findTags(List<Long> tagsId) {
        if (tagsId != null && !tagsId.isEmpty()) {
            log.debug("[TagRepository] Calling findById for multiple tags: {}", tagsId);
            return tagsId.stream()
                    .map(tagId -> tagRepository.findById(tagId)
                            .orElseThrow(() -> new EntityNotFoundException(TAG)))
                    .toList();
        } else {
            return new ArrayList<>();
        }
    }

    private Category findCategory(Long categoryId) {
        if (categoryId != null) {
            log.debug("[CategoryRepository] Calling findById for categoryId={}", categoryId);
            return categoryRepository.findById(categoryId)
                    .orElseThrow(() -> new EntityNotFoundException(CATEGORY));
        } else {
            return null;
        }
    }
}