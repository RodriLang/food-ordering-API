package com.group_three.food_ordering.repositories;

import com.group_three.food_ordering.enums.OrderStatus;
import com.group_three.food_ordering.enums.PaymentStatus;
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

    Optional<Order> findByFoodVenue_IdAndOrderNumberAndCreationDateBetween(
            UUID foodVenueId, Integer orderNumber, LocalDateTime start, LocalDateTime end);

    List<Order> findByFoodVenue_IdAndCreationDateBetweenAndStatus(
            UUID foodVenueId, LocalDateTime start, LocalDateTime end, OrderStatus status
    );

    List<Order> findByFoodVenue_IdAndCreationDateBetween(
            UUID foodVenueId, LocalDateTime start, LocalDateTime end
    );

    List<Order> findByFoodVenue_Id(UUID venueId);

    List<Order> findByFoodVenue_IdAndStatus(UUID venueId, OrderStatus status);

    List<Order> findOrderByTableSession_Id(UUID tableSessionId);

    List<Order> getOrdersByPayment_Status(PaymentStatus status);

     @Query("SELECT COUNT(o) " +
            "FROM Order o " +
            "WHERE o.foodVenue.id = :venueId " +
            "AND o.creationDate >= :start " +
            "AND o.creationDate < :end")
    Long countOrdersToday(
            @Param("venueId") UUID venueId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );
}
