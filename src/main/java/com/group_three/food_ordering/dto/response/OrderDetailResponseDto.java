package com.group_three.food_ordering.dto.response;

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

    //Toma algunos datos de MenuItem y muestra toda la información como un objeto plano
    private Long id;

    private Integer quantity;

    private BigDecimal unitPrice;

    //Precio unitario multiplicado por la cantidad
    private BigDecimal subtotal;

    private String productName;

    private String category;

    private String specialInstructions;
}