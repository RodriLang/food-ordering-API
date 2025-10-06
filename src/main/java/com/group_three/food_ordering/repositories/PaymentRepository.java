package com.group_three.food_ordering.repositories;

import com.group_three.food_ordering.models.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByPublicId(UUID publicId);

    boolean existsByPublicId(UUID publicId);

    void deleteByPublicId(UUID publicId);

}
