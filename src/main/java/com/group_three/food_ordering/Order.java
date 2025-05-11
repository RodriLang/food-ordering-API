package com.group_three.food_ordering;

import com.group_three.food_ordering.enums.OrderStatus;
import com.group_three.food_ordering.models.Client;
import com.group_three.food_ordering.models.FoodVenue;
import com.group_three.food_ordering.models.OrderDetail;
import com.group_three.food_ordering.models.Payment;
import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity(name = "orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private String orderNumber;

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

    @ManyToOne(fetch = FetchType.LAZY)
    private Payment payment;

    @ManyToOne(fetch = FetchType.LAZY)
    private Client client;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails;

    @PrePersist
    public void onCreate() {
        this.creationDate = LocalDateTime.now();
        this.updateDate = LocalDateTime.now();
        if (this.status == null) this.status = OrderStatus.NOT_APPROVED;
        if (this.totalPrice == null) this.totalPrice = BigDecimal.ZERO;
    }

    @PreUpdate
    public void onUpdate() {
        this.updateDate = LocalDateTime.now();
    }
}

