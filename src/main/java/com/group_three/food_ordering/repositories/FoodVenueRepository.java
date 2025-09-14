package com.group_three.food_ordering.repositories;

import com.group_three.food_ordering.models.FoodVenue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface FoodVenueRepository extends JpaRepository<FoodVenue, UUID> {
    Optional<FoodVenue> findByEmailIgnoreCase(String email);

}
