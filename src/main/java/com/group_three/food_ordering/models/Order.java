package com.group_three.food_ordering.models;

import com.group_three.food_ordering.enums.OrderStatus;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "orders")
@SQLDelete(sql = "UPDATE orders SET deleted = true WHERE id = ?")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
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
    private Client client;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "table_session_id")
    private TableSession tableSession;

    @OneToMany(mappedBy = "order", cascade = CascadeType.PERSIST, fetch = FetchType.LAZY)
    private List<OrderDetail> orderDetails = new ArrayList<>();

    @Column(nullable = false)
    private Boolean deleted;

    @PrePersist
    public void onCreate() {
        this.creationDate = LocalDateTime.now();
        this.updateDate = LocalDateTime.now();
        if (this.status == null) this.status = OrderStatus.PENDING;
        if (this.totalPrice == null) this.totalPrice = BigDecimal.ZERO;
        if (this.deleted == null) this.deleted = false;
    }

    @PreUpdate
    public void onUpdate() {
        this.updateDate = LocalDateTime.now();
    }
}

