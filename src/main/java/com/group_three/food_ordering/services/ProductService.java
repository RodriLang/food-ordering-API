package com.group_three.food_ordering.services;

import com.group_three.food_ordering.dto.request.ProductRequestDto;
import com.group_three.food_ordering.dto.response.ItemMenuResponseDto;
import com.group_three.food_ordering.dto.response.ProductResponseDto;
import com.group_three.food_ordering.enums.CloudinaryFolder;
import com.group_three.food_ordering.exceptions.InsufficientStockException;
import com.group_three.food_ordering.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface ProductService {

    ProductResponseDto create(ProductRequestDto productRequestDto, MultipartFile image, CloudinaryFolder cloudinaryFolder);

    ProductResponseDto update(UUID publicId, ProductRequestDto productRequestDto, MultipartFile image, CloudinaryFolder folder);

    ProductResponseDto getById(UUID publicId);

    Product getEntityById(UUID publicId);

    Product getEntityByNameAndContext(String name);

    ItemMenuResponseDto getByNameAndContext(String name);

    void delete(UUID publicId);

    Page<ProductResponseDto> getAll(Pageable pageable);

    Page<ProductResponseDto> getAllAvailable(Pageable pageable);

    void validateStock(Product product, Integer quantity) throws InsufficientStockException;

    void incrementStockProduct(Product product, Integer quantity);

    void decrementStockProduct(Product product, Integer quantity);

    Page<ItemMenuResponseDto> getTopSellingProducts(int limit, int days, Pageable pageable);
}
