package com.group_three.food_ordering.repositories;

import com.group_three.food_ordering.enums.TableStatus;
import com.group_three.food_ordering.models.Table;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ITableRepository extends JpaRepository<Table, Long> {
    List<Table> findByFoodVenueId(UUID foodVenueId);
    Optional<Table> findByFoodVenueIdAndId(UUID foodVenueId, Long id);
    List<Table> findByFoodVenueIdAndStatus(UUID foodVenueId, TableStatus status);
}
