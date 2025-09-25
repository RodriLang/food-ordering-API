package com.group_three.food_ordering.models;

import com.group_three.food_ordering.enums.RoleType;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
public class Employment {

    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "food_venue_id", nullable = false)
    private FoodVenue foodVenue;

    @Column(nullable = false)
    private RoleType role;

    @Column(nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime creationDate;

    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime lastUpdateDate;

    private Boolean active;

    @PrePersist
    public void onCreate() {
        if (this.id == null) this.id = UUID.randomUUID();
        if (this.active == null) this.active = Boolean.TRUE;
        creationDate = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        lastUpdateDate = LocalDateTime.now();
    }
}
