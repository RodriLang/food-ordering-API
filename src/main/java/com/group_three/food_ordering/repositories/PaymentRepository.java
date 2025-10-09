package com.group_three.food_ordering.repositories;

import com.group_three.food_ordering.enums.PaymentStatus;
import com.group_three.food_ordering.models.Order;
import com.group_three.food_ordering.models.Payment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByPublicId(UUID publicId);

    Page<Payment> findByOrders(List<Order> orders, Pageable pageable);

    Page<Payment> findByOrdersAndStatusAndCreationDateBetween(List<Order> orders, PaymentStatus status, LocalDateTime from, LocalDateTime to, Pageable pageable);

    boolean existsByPublicId(UUID publicId);

    void deleteByPublicId(UUID publicId);

}
