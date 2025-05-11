package com.group_three.food_ordering.repositories;

import com.group_three.food_ordering.entities.FoodVenueEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface FoodVenueRepository extends JpaRepository<FoodVenueEntity, UUID> {
}
