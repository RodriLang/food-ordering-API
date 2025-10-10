package com.group_three.food_ordering.repositories;

import com.group_three.food_ordering.models.FeaturedProduct;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FeaturedProductRepository extends JpaRepository<FeaturedProduct, Long> {

    Optional<FeaturedProduct> findByPublicId(UUID publicId);


    boolean existsByProduct_NameAndProduct_FoodVenue_PublicId(String productName, UUID foodVenuePublicId);

    void deleteByProduct_NameAndProduct_FoodVenue_PublicId(String productName, UUID foodVenuePublicId);

    @Query("""
                SELECT fp
                FROM FeaturedProduct fp
                WHERE (fp.featuredFrom IS NULL OR fp.featuredFrom <= CURRENT_TIMESTAMP)
                AND (fp.featuredUntil IS NULL OR fp.featuredUntil >= CURRENT_TIMESTAMP)
                AND fp.active = true
                AND fp.product.name = :productName
                AND fp.product.foodVenue.publicId = :foodVenuePublicId
            """)
    Optional<FeaturedProduct> findActiveByProduct(String productName, UUID foodVenuePublicId);

    @Query("""
                SELECT fp
                FROM FeaturedProduct fp
                WHERE (fp.featuredFrom IS NULL OR fp.featuredFrom <= CURRENT_TIMESTAMP)
                AND (fp.featuredUntil IS NULL OR fp.featuredUntil >= CURRENT_TIMESTAMP)
                AND fp.active = true
                ORDER BY fp.priority ASC NULLS LAST, fp.lastUpdateDate DESC
            """)
    Page<FeaturedProduct> findActiveFeaturedProducts(Pageable pageable);

}


