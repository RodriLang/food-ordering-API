package com.group_three.food_ordering.services;

import com.group_three.food_ordering.dto.request.ProductCreateDto;
import com.group_three.food_ordering.dto.response.ProductResponseDto;
import com.group_three.food_ordering.dto.update.ProductUpdateDto;
import com.group_three.food_ordering.exceptions.InsufficientStockException;
import com.group_three.food_ordering.models.Product;

import java.util.List;

public interface ProductService {
    ProductResponseDto create(ProductCreateDto productCreateDto);
    ProductResponseDto update(Long id, ProductUpdateDto productUpdateDto);
    ProductResponseDto replace(Long id, ProductCreateDto productCreateDto);
    ProductResponseDto getById(Long id);
    void delete(Long id);
    List<ProductResponseDto> getAll();

    List<ProductResponseDto> getAllAvailable();
    void validateStock(Product product, Integer quantity) throws InsufficientStockException;
    void incrementStockProduct(Product product, Integer quantity);
    void decrementStockProduct (Product product, Integer quantity);

}
