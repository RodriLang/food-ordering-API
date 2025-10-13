package com.group_three.food_ordering.repositories;

import com.group_three.food_ordering.models.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findByPublicIdAndDeletedFalse(UUID publicId);

    List<Product> findByNameAndFoodVenue_PublicIdAndDeletedFalse(String name, UUID foodVenueId);

    Page<Product> findAllByFoodVenue_PublicIdAndDeletedFalse(UUID foodVenueId, Pageable pageable);

    Page<Product> findAllByDeletedFalse(Pageable pageable);

    Page<Product> findAllByFoodVenue_PublicIdAndAvailableAndDeletedFalse(UUID foodVenueId, Boolean available, Pageable pageable);

    List<Product> findAllByFoodVenue_PublicIdAndAvailableAndCategoryPublicIdAndDeletedFalse(UUID foodVenueId, Boolean available, UUID categoryId);

    void deleteByPublicId(UUID publicId);

    @Query("""
    SELECT od.product AS product, SUM(od.quantity) AS totalSold
    FROM OrderDetail od
    JOIN Product p ON od.product.id = p.id
    JOIN FeaturedProduct fp ON p.id = fp.product.id
    WHERE fp.creationDate >= :fromDate
    AND fp.deleted = false
    AND p.deleted = false
    GROUP BY od.product
    ORDER BY totalSold DESC
""")
    Page<Product> findTopSellingProducts(@Param("fromDate") Instant fromDate, Pageable pageable);

}
