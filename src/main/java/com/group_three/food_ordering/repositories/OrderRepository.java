package com.group_three.food_ordering.repositories;

import com.group_three.food_ordering.enums.OrderStatus;
import com.group_three.food_ordering.enums.PaymentStatus;
import com.group_three.food_ordering.models.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    Optional<Order> findByFoodVenue_IdAndOrderNumberAndCreationDateBetween(
            UUID foodVenueId, Integer orderNumber, LocalDateTime start, LocalDateTime end);

    Optional<Order> findByIdAndFoodVenue_Id(UUID id, UUID foodVenueId);

    Page<Order> findByFoodVenue_IdAndCreationDateBetweenAndStatus(
            UUID foodVenueId, LocalDateTime start, LocalDateTime end, OrderStatus status, Pageable pageable);

    Page<Order> findByFoodVenue_IdAndCreationDateBetween(
            UUID foodVenueId, LocalDateTime start, LocalDateTime end, Pageable pageable
    );

    Page<Order> findByFoodVenue_Id(UUID venueId, Pageable pageable);

    Page<Order> findByFoodVenue_IdAndStatus(UUID venueId, OrderStatus status, Pageable pageable);

    Page<Order> findOrderByTableSession_Id(UUID tableSessionId, Pageable pageable);

    Page<Order> findOrderByTableSession_IdAndStatus(UUID tableSessionId, OrderStatus status, Pageable pageable);

    Page<Order> findOrdersByPayment_Status(PaymentStatus status, Pageable pageable);

    Page<Order> findOrdersByParticipant_Id(UUID participantId, Pageable pageable);

    Page<Order> findOrdersByParticipant_IdAndStatus(UUID participantId, OrderStatus status, Pageable pageable);

    Page<Order> findOrdersByParticipant_IdAndTableSession_IdAndStatus(UUID participantId, UUID tableSessionId , OrderStatus status, Pageable pageable);

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
