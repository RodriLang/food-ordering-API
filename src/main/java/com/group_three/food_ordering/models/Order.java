package com.group_three.food_ordering.models;

import com.group_three.food_ordering.enums.OrderStatus;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@SQLDelete(sql = "UPDATE orders SET deleted = true WHERE id = ?")
@Getter
@Setter
@EqualsAndHashCode
@ToString(exclude = "foodVenue")
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order {

    @Id
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "id", length = 36)
    private UUID id;

    @Column
    private Integer orderNumber;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Column
    private BigDecimal totalPrice;

    @Column(updatable = false)
    private LocalDateTime creationDate;

    private LocalDateTime updateDate;
    @Column
    private String specialRequirements;

    @ManyToOne(fetch = FetchType.LAZY)
    private FoodVenue foodVenue;

    @ManyToOne
    @JoinColumn(name = "payment_id")
    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY)
    private Participant participant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_session_id")
    private TableSession tableSession;

    @OneToMany(cascade = CascadeType.PERSIST, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "order_id")
    private List<OrderDetail> orderDetails = new ArrayList<>();

    @Column
    private Boolean deleted;

    @PrePersist
    public void onCreate() {
        if (deleted == null) deleted = false;
        this.creationDate = LocalDateTime.now();
        this.updateDate = LocalDateTime.now();
        if (this.id == null) this.id = UUID.randomUUID();
        if (this.status == null) this.status = OrderStatus.PENDING;
    }

    @PreUpdate
    public void onUpdate() {
        this.updateDate = LocalDateTime.now();
    }
}