package com.group_three.food_ordering.repositories;

import com.group_three.food_ordering.models.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IOrderDetailRepository extends JpaRepository<OrderDetail, Long> {

    Optional<OrderDetail> findByIdAndOrder_Id(Long id, UUID orderId);
    List<OrderDetail> findAllByOrder_Id(UUID orderId);

}
