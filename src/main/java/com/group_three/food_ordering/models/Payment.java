package com.group_three.food_ordering.models;

import com.group_three.food_ordering.enums.PaymentMethod;
import com.group_three.food_ordering.enums.PaymentStatus;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.SQLDelete;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "payments")
@SQLDelete(sql = "UPDATE payments SET deleted = true WHERE id = ?")
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
@ToString(exclude = "orders")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@SuperBuilder
public class Payment extends BaseEntity {

    @Column
    private BigDecimal amount;

    @OneToMany(mappedBy = "payment", fetch = FetchType.EAGER)
    private List<Order> orders;

    @Column(name = "payment_date", updatable = false)
    private LocalDateTime paymentDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PaymentMethod paymentMethod;

}
