package com.group_three.food_ordering.analytics.metrics_repositories;

import com.group_three.food_ordering.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.UUID;

@Repository
public interface PaymentMetricsRepository extends JpaRepository<Payment, Long> {

    @Query("""
        SELECT AVG(p.amount)
        FROM Payment p
        JOIN p.orders o
        JOIN o.tableSession ts
        JOIN ts.diningTable dt
        JOIN dt.foodVenue v
        WHERE v.publicId = :venueId
          AND p.status = 'SUCCESS'
          AND p.paymentDate BETWEEN :from AND :to
    """)
    Double findAverageSpending(UUID venueId, LocalDateTime from, LocalDateTime to);

}
