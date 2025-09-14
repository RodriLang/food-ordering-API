package com.group_three.food_ordering.services.impl;

import com.group_three.food_ordering.context.TenantContext;
import com.group_three.food_ordering.dto.request.ProductCreateDto;
import com.group_three.food_ordering.dto.response.ProductResponseDto;
import com.group_three.food_ordering.dto.update.ProductUpdateDto;
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

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final TagRepository tagRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final TenantContext tenantContext;


    @Override
    public ProductResponseDto create(ProductCreateDto productCreateDto) {
        Product product = productMapper.toEntity(productCreateDto);

        FoodVenue foodVenue = new FoodVenue();
        foodVenue.setId(tenantContext.getCurrentFoodVenue().getId());
        product.setFoodVenue(foodVenue);

        product.setAvailable(product.getStock() != null && product.getStock() > 0);

        if (productCreateDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(productCreateDto.getCategoryId())
                    .orElseThrow(() -> new EntityNotFoundException("Category", productCreateDto.getCategoryId().toString()));
            product.setCategory(category);
        }

        if (productCreateDto.getTagsId() != null && !productCreateDto.getTagsId().isEmpty()) {
            List<Tag> tags = tagRepository.findAllById(productCreateDto.getTagsId());
            product.setTags(new ArrayList<>(tags));
        } else {
            product.setTags(new ArrayList<>());
        }
        return productMapper.toDTO(productRepository.save(product));
    }

    @Override
    public ProductResponseDto update(Long id, ProductUpdateDto productUpdateDto) {
        Product product = productRepository.findById(id).orElseThrow(NoSuchElementException::new);

        if (productUpdateDto.getName() != null) product.setName(productUpdateDto.getName());
        if (productUpdateDto.getDescription() != null) product.setDescription(productUpdateDto.getDescription());
        if (productUpdateDto.getPrice() != null) product.setPrice(productUpdateDto.getPrice());
        if (productUpdateDto.getStock() != null) {
            product.setStock(productUpdateDto.getStock());
            product.setAvailable(productUpdateDto.getStock() > 0);
        }
        if (productUpdateDto.getImageUrl() != null) product.setImageUrl(productUpdateDto.getImageUrl());

        if (productUpdateDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(productUpdateDto.getCategoryId())
                    .orElseThrow(() -> new NoSuchElementException("Category not found"));
            product.setCategory(category);
        }

        if (productUpdateDto.getTagIds() != null) {
            List<Tag> tags = tagRepository.findAllById(productUpdateDto.getTagIds());
            product.setTags(new ArrayList<>(tags));
        }
        return productMapper.toDTO(productRepository.save(product));
    }

    @Override
    public ProductResponseDto replace(Long id, ProductCreateDto productCreateDto) {
        Product product = productRepository.findById(id).orElseThrow(NoSuchElementException::new);

        product.setName(productCreateDto.getName());
        product.setDescription(productCreateDto.getDescription());
        product.setPrice(productCreateDto.getPrice());
        product.setStock(productCreateDto.getStock());
        product.setImageUrl(product.getImageUrl());
        product.setAvailable(productCreateDto.getStock() > 0);

        if (productCreateDto.getTagsId() != null && !productCreateDto.getTagsId().isEmpty()) {
            List<Tag> tags = tagRepository.findAllById(productCreateDto.getTagsId());
            product.setTags(new ArrayList<>(tags));
        } else {
            product.setTags(new ArrayList<>());
        }
        if (productCreateDto.getCategoryId() != null) {
            Category category = categoryRepository.findById(productCreateDto.getCategoryId())
                    .orElseThrow(() -> new NoSuchElementException("Category not found"));
            product.setCategory(category);
        } else {
            product.setCategory(null); // o mantener la actual, según tu lógica de negocio
        }

        return productMapper.toDTO(productRepository.save(product));
    }

    @Override
    public ProductResponseDto getById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(NoSuchElementException::new);
        return productMapper.toDTO(product);
    }

    @Override
    public void delete(Long id) {
        productRepository.deleteById(id);
    }


    @Override
    public List<ProductResponseDto> getAll() {
       return productRepository.findAllByFoodVenue_Id(tenantContext.getCurrentFoodVenue().getId()).stream()
                .map(productMapper::toDTO)
                .toList();
    }

    @Override
    public List<ProductResponseDto> getAllAvailable() {
        return productRepository.findAllByFoodVenue_IdAndAvailable(tenantContext.getCurrentFoodVenue().getId(), true).stream()
                .map(productMapper::toDTO)
                .toList();
    }

    public void validateStock(Product product, Integer quantity) throws InsufficientStockException
    {
        if (product.getStock() < quantity) {
            throw new InsufficientStockException("Insufficient stock for product: " + product.getName());
        }
    }
    public void incrementStockProduct(Product product, Integer quantity)
    {
        if (quantity != null && quantity > 0) {
            product.setStock(product.getStock() + quantity);
            product.setAvailable(product.getStock() + quantity > 0);
        }
    }
    public void decrementStockProduct (Product product, Integer quantity)
    {
        validateStock(product, quantity);
        product.setStock(product.getStock() - quantity);
        product.setAvailable(product.getStock() - quantity > 0);
    }


}
