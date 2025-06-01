package com.group_three.food_ordering.repositories;

import com.group_three.food_ordering.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IClientRepository extends JpaRepository<Client, UUID> {

    Optional<Client> findByUserEntity_Email(String email);

    boolean existsByUserEntity_Email(String email);

    boolean existsByNickname(String nickname);

    List<Client> findAllByUserEntity_RemovedAtIsNull();

    Optional<Client> findByIdAndUserEntity_RemovedAtIsNull(UUID id);
}

