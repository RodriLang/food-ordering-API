package com.group_three.food_ordering.models;

import com.group_three.food_ordering.enums.PaymentMethod;
import com.group_three.food_ordering.enums.PaymentStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity(name = "payments")
@SQLDelete(sql = "UPDATE orders SET deleted = true WHERE id = ?")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Payment {

    @Id
    @JdbcTypeCode(SqlTypes.VARCHAR)
    @Column(name = "id", length = 36)
    private UUID id;

    @Column
    private BigDecimal amount;

    @OneToMany(mappedBy = "payment", cascade = CascadeType.ALL)
    private List<Order> orders;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

    @Column(nullable = false, updatable = false)
    LocalDateTime creationDate;

    @Column
    LocalDateTime modificationDate;

    @Column
    private Boolean deleted;

    @PrePersist
    public void onCreate() {
        if (deleted == null) deleted = false;
        if (this.id == null) this.id = UUID.randomUUID();
        if (this.status == null) this.status = PaymentStatus.PENDING;
        if (this.creationDate == null) this.creationDate = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        if (this.modificationDate == null) this.modificationDate = LocalDateTime.now();
        //calculateAmount(); // recalcular el monto si cambia algo
    }

    public void calculateAmount() {
        if (orders != null) {
            this.amount = this.orders.stream()
                    .map(Order::getTotalPrice)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
        } else {
            this.amount = BigDecimal.ZERO;
        }
    }

}
