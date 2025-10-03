package com.group_three.food_ordering.repositories;

import com.group_three.food_ordering.models.Category;
import com.group_three.food_ordering.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByNameAndFoodVenue_Id(String name, UUID foodVenueId);
    List<Product> findAllByFoodVenue_Id(UUID foodVenueId);
    List<Product> findAllByFoodVenue_IdAndAvailable(UUID foodVenueId, Boolean available);
    List<Product> findAllByFoodVenue_IdAndAvailableAndCategory(UUID foodVenueId, Boolean available, Category category);

}
