package com.group_three.food_ordering.repositories;

import com.group_three.food_ordering.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    boolean existsByEmail(String email);

    List<User> findAllByRemovedAtIsNull();

    Optional<User> findByIdAndRemovedAtIsNull(UUID id);

    List<User> findAllByRemovedAtIsNotNull();

}
