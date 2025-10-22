package com.group_three.food_ordering.models;

import com.group_three.food_ordering.enums.DiningTableStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dining_tables")
@SQLDelete(sql = "UPDATE dining_tables SET deleted = true WHERE id = ?")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(exclude = {"foodVenue", "tableSessions"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class DiningTable extends BaseEntity {

    @Column(nullable = false)
    private Integer number;

    @Column(nullable = false)
    private Integer capacity;

    @Column
    private String qrCodeImageUrl;

    @Enumerated(EnumType.STRING)
    private DiningTableStatus status;

    @OneToMany(mappedBy = "diningTable", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    private List<TableSession> tableSessions = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "food_venue_id")
    private FoodVenue foodVenue;

}