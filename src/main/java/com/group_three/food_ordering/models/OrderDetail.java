package com.group_three.food_ordering.models;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;

import java.math.BigDecimal;

@Getter
@Setter
@ToString(exclude = "order")
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "order_details")
@SQLDelete(sql = "UPDATE order_details SET deleted = true WHERE id = ?")
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer quantity;

    @Column(nullable = false)
    private BigDecimal price;

    @Column(length = 255)
    private String specialInstructions;

    @ManyToOne(optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @ManyToOne(optional = false)
    @JoinColumn(name = "order_id")
    private Order order;

    @Column(nullable = false)
    private Boolean deleted;

    @PrePersist
    public void onCreate() {
        if (this.quantity == null) this.quantity = 1;
        if (this.price == null) {
            this.price = this.product.getPrice().multiply(BigDecimal.valueOf(quantity));
        }
        if (this.deleted == null) this.deleted = false;
    }

    @PreUpdate
    public void onUpdate() {
        if (this.quantity == null) this.quantity = 1;
    }
}
