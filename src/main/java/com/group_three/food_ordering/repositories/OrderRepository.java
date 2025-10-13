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

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    Optional<Order> findByPublicId(UUID publicId);

    Optional<Order> findByFoodVenue_PublicIdAndOrderNumberAndOrderDateBetween(
            UUID foodVenueId, Integer orderNumber, Instant start, Instant end);

    Optional<Order> findByPublicIdAndFoodVenue_PublicId(UUID id, UUID foodVenueId);

    Page<Order> findByFoodVenue_PublicIdAndOrderDateBetweenAndStatus(
            UUID foodVenueId, Instant start, Instant end, OrderStatus status, Pageable pageable);

    Page<Order> findByFoodVenue_PublicIdAndOrderDateBetween(
            UUID foodVenueId, Instant start, Instant end, Pageable pageable
    );

    Page<Order> findByFoodVenue_PublicId(UUID venueId, Pageable pageable);

    Page<Order> findByFoodVenue_PublicIdAndStatus(UUID venueId, OrderStatus status, Pageable pageable);

    Page<Order> findOrderByTableSession_PublicId(UUID tableSessionId, Pageable pageable);

    Page<Order> findOrderByTableSession_PublicIdAndStatus(UUID tableSessionId, OrderStatus status, Pageable pageable);

    Page<Order> findOrdersByPayment_Status(PaymentStatus status, Pageable pageable);

    Page<Order> findOrdersByParticipant_PublicId(UUID participantId, Pageable pageable);

    Page<Order> findOrdersByParticipant_PublicIdAndStatus(UUID participantId, OrderStatus status, Pageable pageable);

    Page<Order> findOrdersByParticipant_PublicIdAndTableSession_PublicIdAndStatus(UUID participantId, UUID tableSessionId, OrderStatus status, Pageable pageable);

    void deleteByPublicId(UUID publicId);

    @Query("SELECT COUNT(o) " +
            "FROM Order o " +
            "WHERE o.foodVenue.publicId = :venuePublicId " +
            "AND o.orderDate >= :start " +
            "AND o.orderDate < :end ")
    Long countOrdersToday(
            @Param("venuePublicId") UUID venuePublicId,
            @Param("start") Instant start,
            @Param("end") Instant end
    );

}
