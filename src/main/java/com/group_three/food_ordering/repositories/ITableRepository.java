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
public interface ITableRepository extends JpaRepository<Table, Long> {
    List<Table> findByFoodVenueId(UUID foodVenueId);
    Optional<Table> findByFoodVenueIdAndId(UUID foodVenueId, Long id);
    Optional<Table> findByFoodVenueIdAndNumber(UUID foodVenueId, Integer number);

    @Query("SELECT t FROM tables t WHERE " +
            "t.foodVenue.id = :foodVenueId AND " +
            "(:status IS NULL OR t.status = :status) AND " +
            "(:capacity IS NULL OR t.capacity = :capacity)")
    List<Table> findByFoodVenueIdAndFilters(
            @Param("foodVenueId") UUID foodVenueId,
            @Param("status") TableStatus status,
            @Param("capacity") Integer capacity
    );
}
