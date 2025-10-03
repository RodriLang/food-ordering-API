package com.group_three.food_ordering.repositories;

import com.group_three.food_ordering.models.Category;
import com.group_three.food_ordering.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByIdAndDeletedFalse(Long id);
    List<Product> findByNameAndFoodVenue_IdAndDeletedFalse(String name, UUID foodVenueId);
    List<Product> findAllByFoodVenue_IdAndDeletedFalse(UUID foodVenueId);
    List<Product> findAllByFoodVenue_IdAndAvailableAndDeletedFalse(UUID foodVenueId, Boolean available);
    List<Product> findAllByFoodVenue_IdAndAvailableAndCategoryAndDeletedFalse(UUID foodVenueId, Boolean available, Category category);

}
