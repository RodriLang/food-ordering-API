package com.group_three.food_ordering.repositories;

import com.group_three.food_ordering.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IOrderRepository extends JpaRepository<Order, UUID> {

        Optional<Order> findByIdAndDeletedFalse(UUID orderId);

        Optional<Order> findByFoodVenue_IdAndOrderNumberAndCreationDateBetweenAndDeletedFalse(
                UUID foodVenueId, Integer orderNumber, LocalDateTime start, LocalDateTime end);

        List<Order> findByFoodVenue_IdAndCreationDateBetweenAndDeletedFalse(
                UUID foodVenueId, LocalDateTime start, LocalDateTime end
        );

        List<Order> findAllByDeletedFalse();

        List<Order> findByFoodVenue_IdAndDeletedFalse(UUID venueId);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.foodVenue = :venueId " +
            "AND o.creationDate >= :start AND o.creationDate < :end")
    Long countOrdersToday(
            @Param("venueId") UUID venueId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );



}
