package com.group_three.food_ordering.strategies.interfaces;

import com.group_three.food_ordering.models.TableSession;

public interface TableSessionPaymentStrategy {

    void pay(TableSession tableSession);

}
