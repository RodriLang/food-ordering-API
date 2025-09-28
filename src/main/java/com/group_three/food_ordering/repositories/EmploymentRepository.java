package com.group_three.food_ordering.repositories;

import com.group_three.food_ordering.models.Employment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmploymentRepository extends JpaRepository<Employment, UUID> {

    Page<Employment> findByUser_IdAndActiveTrue(UUID uuid, Pageable pageable);

    Page<Employment> getAllByActive(Pageable pageable, Boolean active);

    List<Employment> findByUser_IdAndActiveTrue(UUID uuid);

    Optional<Employment> findByIdAndActiveTrue(UUID uuid);

}
