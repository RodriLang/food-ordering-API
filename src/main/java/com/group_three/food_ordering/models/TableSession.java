package com.group_three.food_ordering.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.*;

@Entity(name = "table_sessions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TableSession {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    @ManyToOne
    @JoinColumn(name = "table_id", nullable = false)
    private Table table;

    @ManyToOne
    @JoinColumn(name = "food_venue_id", nullable = false)
    private FoodVenue foodVenue;

    @ManyToOne
    @JoinColumn(name = "host_client_id", nullable = false)
    private Client hostClient;

    @OneToMany(mappedBy = "tableSession", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Order> orders = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "table_session_clients",
            joinColumns = @JoinColumn(name = "table_session_id"),
            inverseJoinColumns = @JoinColumn(name = "client_id")
    )
    private List<Client> participants = new ArrayList<>();
}