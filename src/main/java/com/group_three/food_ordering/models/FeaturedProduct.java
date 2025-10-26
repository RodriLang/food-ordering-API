package com.group_three.food_ordering.models;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;

import java.time.Instant;

@Entity
@Table(name = "featured_products", indexes = {
        @Index(name = "idx_featured_dates", columnList = "featured_from, featured_until"),
        @Index(name = "idx_priority", columnList = "priority")
})
@SQLDelete(sql = "UPDATE employments SET deleted = true WHERE id = ?")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString
@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class FeaturedProduct extends BaseEntity {

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "featured_from", nullable = false)
    @Builder.Default
    private Instant featuredFrom = Instant.now();

    @Column(name = "featured_until")
    private Instant featuredUntil;

    @Column(name = "priority")
    @Builder.Default
    private Integer priority = 0;

    @Column
    private Boolean active;

}