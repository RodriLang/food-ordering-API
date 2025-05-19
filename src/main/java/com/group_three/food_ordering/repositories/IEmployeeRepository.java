package com.group_three.food_ordering.repositories;

import com.group_three.food_ordering.models.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IEmployeeRepository extends JpaRepository<Employee, UUID> {

    Optional<Employee> findByUser_Email(String email);

    boolean existsByUser_Email(String email);

    List<Employee> findAllByUser_RemovedAtIsNull();

    Optional<Employee> findByIdAndUser_RemovedAtIsNull(UUID id);

    List<Employee> findAllByFoodVenue_Id(UUID foodVenueId);
}

