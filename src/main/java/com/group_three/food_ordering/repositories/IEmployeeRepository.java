package com.group_three.food_ordering.repositories;

import com.group_three.food_ordering.models.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IEmployeeRepository extends JpaRepository<Employee, UUID> {

    Optional<Employee> findByUserEntity_Email(String email);

    boolean existsByUserEntity_Email(String email);

    List<Employee> findAllByUserEntity_RemovedAtIsNull();

    Optional<Employee> findByIdAndUserEntity_RemovedAtIsNull(UUID id);

    List<Employee> findAllByFoodVenue_Id(UUID foodVenueId);
}

