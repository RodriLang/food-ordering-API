package com.group_three.food_ordering.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderDetailResponseDto {

    //Take some data from MenuItem and display all the information as a flat object
    private Long id;

    private Integer quantity;
    private BigDecimal unitPrice;

    //Unit price multiplied by quantity
    private BigDecimal totalPrice;

    private String productName;
    private String category;
    private String specialInstructions;
}