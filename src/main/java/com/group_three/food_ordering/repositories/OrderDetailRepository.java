package com.group_three.food_ordering.repositories;

import com.group_three.food_ordering.models.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    List<OrderDetail> findAllByDeletedFalse();

    Optional<OrderDetail> findByIdAndDeletedFalse(Long id);
}
