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


    boolean existsByProduct_PublicId(UUID productPublicId);

    void deleteByProduct_PublicId(UUID productPublicId);

    @Query("""
                SELECT fp
                FROM FeaturedProduct fp
                WHERE (fp.featuredFrom IS NULL OR fp.featuredFrom <= CURRENT_TIMESTAMP)
                AND (fp.featuredUntil IS NULL OR fp.featuredUntil >= CURRENT_TIMESTAMP)
                AND fp.product.id = :productPublicId
            """)
    Optional<FeaturedProduct> findActiveByProduct_PublicId(UUID productPublicId);

    @Query("""
                SELECT fp
                FROM FeaturedProduct fp
                WHERE (fp.featuredFrom IS NULL OR fp.featuredFrom <= CURRENT_TIMESTAMP)
                AND (fp.featuredUntil IS NULL OR fp.featuredUntil >= CURRENT_TIMESTAMP)
                ORDER BY fp.priority ASC NULLS LAST, fp.lastUpdateDate DESC
            """)
    Page<FeaturedProduct> findActiveFeaturedProducts(Pageable pageable);

}


