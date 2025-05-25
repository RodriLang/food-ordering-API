package com.group_three.food_ordering.repositories;

import com.group_three.food_ordering.models.OrderDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IOrderDetailRepository extends JpaRepository<OrderDetail, Long> {


    /**
     * Busca todos los OrderDetail que no están marcados como eliminados.
     * @return lista de detalles activos.
     */
    List<OrderDetail> findAllByDeletedFalse();

    /**
     * Busca todos los OrderDetail asociados a una orden específica y no eliminados.
     * @param orderId id de la orden.
     * @return lista de detalles activos para esa orden.
     */
    List<OrderDetail> findAllByOrder_IdAndDeletedFalse(UUID orderId);

    /**
     * Busca un OrderDetail por su id solo si no está eliminado.
     * @param id id del detalle.
     * @return el detalle si existe y no está eliminado.
     */
    Optional<OrderDetail> findByIdAndDeletedFalse(Long id);

}
