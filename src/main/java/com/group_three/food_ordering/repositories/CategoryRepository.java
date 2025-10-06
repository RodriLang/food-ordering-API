package com.group_three.food_ordering.repositories;

import com.group_three.food_ordering.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByPublicIdAndFoodVenue_PublicId(UUID publicId, UUID foodVenuePublicId);

    List<Category> findByParentCategoryIsNullAndFoodVenue_PublicId(UUID foodVenuePublicId);

    List<Category> findByParentCategoryPublicIdAndFoodVenue_PublicId(UUID id, UUID foodVenuePublicId);

    void deleteByPublicIdAndFoodVenue_PublicId(UUID publicId, UUID foodVenuePublicId);

}
