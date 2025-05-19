package com.group_three.food_ordering.services.interfaces;

import com.group_three.food_ordering.dtos.create.ProductCreateDto;
import com.group_three.food_ordering.dtos.response.ProductResponseDto;
import com.group_three.food_ordering.dtos.update.ProductUpdateDto;
import com.group_three.food_ordering.exceptions.InsufficientStockException;
import com.group_three.food_ordering.models.Product;

import java.util.List;
import java.util.UUID;

public interface IProductService {
    ProductResponseDto create(ProductCreateDto productCreateDto);
    ProductResponseDto update(Long id, ProductUpdateDto productUpdateDto);
    ProductResponseDto replace(Long id, ProductCreateDto productCreateDto);
    ProductResponseDto getById(Long id);
    void delete(Long id);
    List<ProductResponseDto> getAll();

    List<ProductResponseDto> getAllAvailable();
    void validateStock(Product product, Integer quantity) throws InsufficientStockException;
    void IncrementStockProduct (Product product, Integer quantity);
    void decrementStockProduct (Product product, Integer quantity);

}
