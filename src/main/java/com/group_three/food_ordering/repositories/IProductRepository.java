package com.group_three.food_ordering.repositories;

import com.group_three.food_ordering.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface IProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByNameAndFoodVenue_Id(String name, UUID foodVenueId);
    List<Product> findAllByFoodVenue_Id(UUID foodVenueId);
    List<Product> findAllByFoodVenue_IdAndAvailable(UUID foodVenueId, Boolean available);


}
