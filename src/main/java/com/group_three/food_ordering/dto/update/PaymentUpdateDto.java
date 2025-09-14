package com.group_three.food_ordering.dto.update;

import com.group_three.food_ordering.enums.PaymentMethod;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
public class PaymentUpdateDto {

    private PaymentMethod paymentMethod;

    private List<UUID> orderIds = new ArrayList<>();
}
