package com.group_three.food_ordering.models;

import com.group_three.food_ordering.enums.RoleType;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table(name = "employments")
@SQLDelete(sql = "UPDATE employments SET deleted = true WHERE id = ?")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(exclude = {"foodVenue", "user"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class Employment extends BaseEntity {

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "food_venue_id", nullable = false)
    private FoodVenue foodVenue;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleType role;

    @Column
    private Boolean active;

}
