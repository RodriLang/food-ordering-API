package com.group_three.food_ordering.repositories;

import com.group_three.food_ordering.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByPublicIdAndFoodVenue_PublicIdAndDeletedFalse(UUID publicId, UUID foodVenuePublicId);

    List<Category> findByParentCategoryIsNullAndFoodVenue_PublicIdAndDeletedFalse(UUID foodVenuePublicId);

    List<Category> findByParentCategoryPublicIdAndFoodVenue_PublicIdAndDeletedFalse(UUID id, UUID foodVenuePublicId);

    List<Category> findAllByFoodVenue_PublicIdAndParentCategoryIsNullAndDeletedFalse(UUID foodVenuePublicId);

    void deleteByPublicIdAndFoodVenue_PublicIdAndDeletedFalse(UUID publicId, UUID foodVenuePublicId);

}
