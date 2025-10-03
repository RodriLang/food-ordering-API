package com.group_three.food_ordering.repositories;

import com.group_three.food_ordering.enums.TableStatus;
import com.group_three.food_ordering.models.Table;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TableRepository extends JpaRepository<Table, UUID> {

    List<Table> findByFoodVenueIdAndDeletedFalse(UUID foodVenueId);

    Optional<Table> findByIdAndDeletedFalse(UUID id);

    Optional<Table> findByFoodVenueIdAndNumberAndDeletedFalse(UUID foodVenueId, Integer number);

    @Query("SELECT t FROM tables t WHERE " +
            "t.foodVenue.id = :foodVenueId AND " +
            "(:status IS NULL OR t.status = :status) AND " +
            "(:capacity IS NULL OR t.capacity = :capacity) " +
            "AND t.deleted = false")
    List<Table> findByFoodVenueIdAndFiltersAndDeletedFalse(
            @Param("foodVenueId") UUID foodVenueId,
            @Param("status") TableStatus status,
            @Param("capacity") Integer capacity
    );
}
