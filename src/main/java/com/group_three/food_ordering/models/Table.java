package com.group_three.food_ordering.models;

import com.group_three.food_ordering.enums.TableStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity(name = "tables")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Table {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer number;

    @Column(nullable = false)
    private Integer capacity;

    @Enumerated(EnumType.STRING)
    private TableStatus status;

    @ManyToOne
    @JoinColumn(name = "food_venue_id", nullable = false)
    private FoodVenue foodVenue;
}
