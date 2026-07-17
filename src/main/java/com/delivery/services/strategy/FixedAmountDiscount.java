package com.delivery.services.strategy;

import com.delivery.exception.ValidationException;
import com.delivery.model.Order;
import com.delivery.model.OrderItem;

public class FixedAmountDiscount implements IDiscountStrategy {
    private final double discountAmount;
    private final double minOrderValue;

    public FixedAmountDiscount(double discountAmount, double minOrderValue) {
        this.discountAmount = discountAmount;
        this.minOrderValue = minOrderValue;
    }

    @Override
    public double calculateDiscount(Order order) {
        if (order == null || order.getItems() == null)
            return 0.0;
        double subTotal = 0;
        for (OrderItem item : order.getItems()) {
            subTotal += item.calculateItemPrice();
        }
        if (subTotal < minOrderValue) {
            throw new ValidationException(
                    String.format("This coupon only applies to orders of %,.0f VND or more (Current: %,.0f VND).",
                            minOrderValue, subTotal));
        }
        return discountAmount;
    }

}
