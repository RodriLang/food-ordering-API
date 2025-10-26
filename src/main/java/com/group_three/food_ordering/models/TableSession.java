package com.group_three.food_ordering.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;

import java.time.Instant;
import java.util.*;

@Entity
@Table(name = "table_sessions")
@SQLDelete(sql = "UPDATE table_sessions SET deleted = true WHERE id = ?")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(exclude = {"foodVenue", "orders", "participants"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class TableSession extends BaseEntity {

    @Column(name = "start_time", nullable = false)
    private Instant startTime;

    @Column(name = "end_time")
    private Instant endTime;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "dining_table_id", nullable = false)
    private DiningTable diningTable;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "food_venue_id", nullable = false)
    private FoodVenue foodVenue;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "host_id")
    private Participant sessionHost;

    @Builder.Default
    @OneToMany(
            mappedBy = "tableSession",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE},
            orphanRemoval = true,
            fetch = FetchType.EAGER
    )
    private List<Order> orders = new ArrayList<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "table_session_participants",
            joinColumns = @JoinColumn(name = "table_session_id"),
            inverseJoinColumns = @JoinColumn(name = "participant_id")
    )
    @Builder.Default
    private List<Participant> participants = new ArrayList<>();

}
