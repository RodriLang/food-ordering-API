package com.group_three.food_ordering.repositories;

import com.group_three.food_ordering.models.FoodVenue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface FoodVenueRepository extends JpaRepository<FoodVenue, Long> {

    Optional<FoodVenue> findByEmailIgnoreCase(String email);

    Optional<FoodVenue> findByPublicId(UUID publicId);

    @Query("SELECT fv FROM FoodVenue fv WHERE fv.deleted = true")
    Page<FoodVenue> findAllDeleted(Pageable pageable);
}
