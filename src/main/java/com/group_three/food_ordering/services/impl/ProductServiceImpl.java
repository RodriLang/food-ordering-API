package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.context.TenantContext;
import com.group_three.food_ordering.dto.request.ProductRequestDto;
import com.group_three.food_ordering.dto.response.ItemMenuResponseDto;
import com.group_three.food_ordering.dto.response.ProductResponseDto;
import com.group_three.food_ordering.enums.CloudinaryFolder;
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
import com.group_three.food_ordering.services.CloudinaryService;
import com.group_three.food_ordering.services.ProductService;
import com.group_three.food_ordering.services.TagService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.*;

import static com.group_three.food_ordering.utils.EntityName.PRODUCT;
import static com.group_three.food_ordering.utils.EntityName.TAG;
import static com.group_three.food_ordering.utils.EntityName.CATEGORY;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final TagRepository tagRepository;
    private final TagService tagService;
    private final CategoryRepository categoryRepository;
    private final CloudinaryService cloudinaryService;
    private final ProductMapper productMapper;
    private final TenantContext tenantContext;

    @Override
    @Transactional
    public ProductResponseDto create(ProductRequestDto productRequestDto, MultipartFile image, CloudinaryFolder folder) {
        log.info("Creating product: {}", productRequestDto.getName());

        // 1. Mapear a entidad
        Product product = productMapper.toEntity(productRequestDto);

        // 2. Asignar venue actual
        FoodVenue foodVenue = tenantContext.requireFoodVenue();
        product.setFoodVenue(foodVenue);

        // 3. Subir imagen si existe
        if (image != null && !image.isEmpty()) {
            log.debug("Uploading image to Cloudinary for venue '{}': {}",
                    foodVenue.getName(), image.getOriginalFilename());

            String imageUrl = cloudinaryService.uploadImage(image, foodVenue.getName(), folder);
            product.setImageUrl(imageUrl);
        }

        // 4. Aplicar reglas de negocio
        product.setPrice(productRequestDto.getPrice() != null ? productRequestDto.getPrice() : BigDecimal.ZERO);
        product.setStock(productRequestDto.getStock() != null ? productRequestDto.getStock() : 0);
        product.setAvailable(product.getStock() > 0);
        product.setCategory(findCategory(productRequestDto.getCategoryId()));

        product.setTags(new HashSet<>(findTags(productRequestDto.getTags())));

        // 5. Guardar
        log.debug("[ProductRepository] Calling save to create new product for venue {}", foodVenue.getPublicId());
        Product savedProduct = productRepository.save(product);

        return productMapper.toDto(savedProduct);
    }

    @Override
    @Transactional
    public ProductResponseDto update(UUID publicId, ProductRequestDto productRequestDto) {
        Product product = getEntityById(publicId);
        productMapper.updateEntity(product, productRequestDto);
        log.debug("[ProductService] Applying rules and saving update for product {}", publicId);
        return applyProductRulesAndSave(product, productRequestDto);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponseDto getById(UUID publicId) {
        log.debug("[ProductRepository] Calling findByPublicId for product publicId={}", publicId);
        Product product = productRepository.findByPublicIdAndDeletedFalse(publicId)
                .orElseThrow(() -> new EntityNotFoundException(PRODUCT));
        return productMapper.toDto(product);
    }

    @Override
    public Product getEntityById(UUID publicId) {
        log.debug("[ProductRepository] Calling findAndLockByPublicId for product publicId={}", publicId);
        return productRepository.findAndLockByPublicIdAndDeletedFalse(publicId)
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
    @Transactional
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

        Set<Tag> tags = product.getTags();
        tags.clear();

        Set<Tag> newTags = tagService.createIfNotExists(productRequestDto.getTags());
        if (!newTags.isEmpty()) {
            tags.addAll(newTags);
        }
        Product savedProduct = productRepository.save(product);
        ProductResponseDto productResponseDto = productMapper.toDto(savedProduct);
        log.debug("[ProductService] Product updated successfully = {}", productResponseDto);
        return productResponseDto;
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

    @Transactional
    public void incrementStockProduct(Product product, Integer quantity) {
        if (quantity != null && quantity > 0) {
            log.debug("[ProductService] Incrementing stock for product {} by {}", product.getPublicId(), quantity);
            int newStock = product.getStock() + quantity;
            product.setStock(newStock);
            product.setAvailable(newStock > 0);
            // productRepository.save(product);
        }
    }

    @Transactional
    public void decrementStockProduct(Product product, Integer quantity) {
        validateStock(product, quantity);
        log.debug("[ProductService] Decrementing stock for product {} by {}", product.getPublicId(), quantity);
        int newStock = product.getStock() - quantity;
        product.setStock(newStock);
        product.setAvailable(newStock > 0);
        // productRepository.save(product);
    }

    private Set<Tag> findTags(List<String> tagLabels) {
        if (tagLabels != null && !tagLabels.isEmpty()) {
            log.debug("[TagRepository] Calling findById for multiple tags: {}", tagLabels);
            Set<Tag> tags = tagRepository.findAllByLabelIn(tagLabels);
            if (tags.size() != tagLabels.size()) {
                throw new EntityNotFoundException(TAG);
            }
            return new HashSet<>(tags);
        } else {
            return new HashSet<>();
        }
    }

    private Category findCategory(UUID categoryId) {
        log.debug("[CategoryRepository] Calling findById for categoryId={}", categoryId);
        if (categoryId != null) {
            Category category = categoryRepository.findByPublicIdAndFoodVenue_PublicIdAndDeletedFalse(categoryId, tenantContext.getFoodVenueId())
                    .orElseThrow(() -> new EntityNotFoundException(CATEGORY));
            log.debug("[Product Service] Category found={}", category.getName());
            return category;
        } else {
            return null;
        }
    }
}