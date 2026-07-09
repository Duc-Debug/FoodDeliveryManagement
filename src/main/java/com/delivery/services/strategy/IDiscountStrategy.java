package com.delivery.services.strategy;

import com.delivery.model.Order;

public interface IDiscountStrategy {
    double calculateDiscount(Order order);
}
