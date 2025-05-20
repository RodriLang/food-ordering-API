package com.group_three.food_ordering.models;

import com.group_three.food_ordering.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Entity(name = "payments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column
    private BigDecimal amount;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL)
    private List<Order> orders;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Column(nullable = false)
    private Boolean deleted;

    @PrePersist
    public void onCreate() {
        if(this.status == null) this.status = PaymentStatus.PENDING;
        if(this.deleted == null) this.deleted = Boolean.FALSE;
    }

}
