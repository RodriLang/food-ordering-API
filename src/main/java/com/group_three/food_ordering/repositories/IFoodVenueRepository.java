package com.group_three.food_ordering.repositories;

import com.group_three.food_ordering.models.FoodVenue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface IFoodVenueRepository extends JpaRepository<FoodVenue, UUID> {
}
