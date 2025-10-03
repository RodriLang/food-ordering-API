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

    Optional<Order> findByFoodVenue_IdAndOrderNumberAndCreationDateBetweenAndDeletedFalse(
            UUID foodVenueId, Integer orderNumber, LocalDateTime start, LocalDateTime end);

    Optional<Order> findByIdAndFoodVenue_Id(UUID id, UUID foodVenueId);

    Page<Order> findByFoodVenue_IdAndCreationDateBetweenAndStatusAndDeletedFalse(
            UUID foodVenueId, LocalDateTime start, LocalDateTime end, OrderStatus status, Pageable pageable);

    Page<Order> findByFoodVenue_IdAndCreationDateBetweenAndDeletedFalse(
            UUID foodVenueId, LocalDateTime start, LocalDateTime end, Pageable pageable
    );

    Page<Order> findByFoodVenue_IdAndDeletedFalse(UUID venueId, Pageable pageable);

    Page<Order> findByFoodVenue_IdAndStatusAndDeletedFalse(UUID venueId, OrderStatus status, Pageable pageable);

    Page<Order> findOrderByTableSession_IdAndDeletedFalse(UUID tableSessionId, Pageable pageable);

    Page<Order> findOrderByTableSession_IdAndStatusAndDeletedFalse(UUID tableSessionId, OrderStatus status, Pageable pageable);

    Page<Order> findOrdersByPayment_StatusAndDeletedFalse(PaymentStatus status, Pageable pageable);

    Page<Order> findOrdersByParticipant_IdAndDeletedFalse(UUID participantId, Pageable pageable);

    Page<Order> findOrdersByParticipant_IdAndStatusAndDeletedFalse(UUID participantId, OrderStatus status, Pageable pageable);

    Page<Order> findOrdersByParticipant_IdAndTableSession_IdAndStatusAndDeletedFalse(UUID participantId, UUID tableSessionId, OrderStatus status, Pageable pageable);

    @Query("SELECT COUNT(o) " +
            "FROM Order o " +
            "WHERE o.foodVenue.id = :venueId " +
            "AND o.creationDate >= :start " +
            "AND o.creationDate < :end " +
            "AND o.deleted = false")
    Long countOrdersToday(
            @Param("venueId") UUID venueId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

}
