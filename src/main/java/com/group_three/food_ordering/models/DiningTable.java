package com.group_three.food_ordering.models;

import com.group_three.food_ordering.enums.DiningTableStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "dining_tables")
@SQLDelete(sql = "UPDATE dining_tables SET deleted = true WHERE id = ?")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(exclude = {"foodVenue", "tableSessions"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class DiningTable extends BaseEntity {

    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "public_id", length = 36, unique = true, nullable = false, updatable = false)
    @EqualsAndHashCode.Include
    private UUID publicId;

    @Column(nullable = false)
    private Integer number;

    @Column(nullable = false)
    private Integer capacity;

    @Enumerated(EnumType.STRING)
    private DiningTableStatus status;

    @OneToMany(mappedBy = "diningTable", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TableSession> tableSessions = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "food_venue_id")
    private FoodVenue foodVenue;

}