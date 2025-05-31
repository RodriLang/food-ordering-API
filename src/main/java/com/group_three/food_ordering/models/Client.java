package com.group_three.food_ordering.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity(name = "clients")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = true)
    private UserEntity user;

    @Column(nullable = false, unique = true)
    private String nickname;
}
