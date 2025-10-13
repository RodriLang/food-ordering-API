package com.group_three.food_ordering.models;

import com.group_three.food_ordering.enums.OrderStatus;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@SQLDelete(sql = "UPDATE orders SET deleted = true WHERE id = ?")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(exclude = {"foodVenue", "participant", "tableSession", "orderDetails"})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class Order extends BaseEntity {

    @Column
    private Integer orderNumber;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column
    private BigDecimal totalPrice;

    @Column(name = "order_date", updatable = false)
    private Instant orderDate;

    @Column
    private String specialRequirements;

    @ManyToOne
    private FoodVenue foodVenue;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @ManyToOne (fetch = FetchType.EAGER)
    private Participant participant;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "table_session_id")
    private TableSession tableSession;

    @OneToMany(cascade = CascadeType.PERSIST, orphanRemoval = true, fetch = FetchType.EAGER)
    @JoinColumn(name = "order_id")
    @Builder.Default
    private List<OrderDetail> orderDetails = new ArrayList<>();

}
