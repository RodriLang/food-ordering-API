package com.group_three.food_ordering.models;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "food_venue_id", nullable = false)
    private FoodVenue foodVenue;

    @Column(nullable = false)
    private String position;

    @PrePersist
    public void onCreate() {
        if (this.id == null) this.id = UUID.randomUUID();
    }
}
