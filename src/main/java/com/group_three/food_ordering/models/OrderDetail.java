package com.group_three.food_ordering.models;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

<<<<<<< HEAD
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity (name = "order_details")
public class OrderDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false)
    private Integer quantity;

    @Column (nullable = false)
    private BigDecimal price;

    @Column (length = 255)
    private String specialInstructions;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_item_id", nullable = false)
    private MenuItem menuItem;

}
=======
import java.util.UUID;

@Entity(name = "order_details")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private Order order;
}
>>>>>>> 51b6d069bf85c3e1a3fade8bf7a763a32e77820e
