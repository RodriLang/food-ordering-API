package com.group_three.food_ordering.repositories;

import com.group_three.food_ordering.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IClientRepository extends JpaRepository<Client, UUID> {

    Optional<Client> findByUser_Email(String email);

    boolean existsByUser_Email(String email);

    boolean existsByNickname(String nickname);

    List<Client> findAllByUser_RemovedAtIsNull();

    Optional<Client> findByIdAndUser_RemovedAtIsNull(UUID id);
}

