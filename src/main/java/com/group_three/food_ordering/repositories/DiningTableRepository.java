package com.group_three.food_ordering.repositories;

import com.group_three.food_ordering.enums.DiningTableStatus;
import com.group_three.food_ordering.models.DiningTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DiningTableRepository extends JpaRepository<DiningTable, Long> {

    Optional<DiningTable> findByPublicId(UUID publicId);

    List<DiningTable> findByFoodVenuePublicId(UUID foodVenueId);

    Optional<DiningTable> findByFoodVenuePublicIdAndNumber(UUID foodVenueId, Integer number);

    @Query("SELECT t FROM DiningTable t WHERE " +
            "t.foodVenue.publicId = :foodVenuePublicId AND " +
            "(:status IS NULL OR t.status = :status) AND " +
            "(:capacity IS NULL OR t.capacity = :capacity) ")
    List<DiningTable> findByFoodVenuePublicIdAndFiltersAndDeletedFalse(
            @Param("foodVenuePublicId") UUID foodVenuePublicId,
            @Param("status") DiningTableStatus status,
            @Param("capacity") Integer capacity
    );
}
