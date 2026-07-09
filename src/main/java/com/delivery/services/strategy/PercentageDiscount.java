package com.delivery.services.strategy;

import com.delivery.model.Order;
import com.delivery.model.OrderItem;

public class PercentageDiscount implements IDiscountStrategy {
    private final double percentage;
    private final double maxDiscountAmount;
    public PercentageDiscount(double percentage, double maxDiscountAmount){
        this.percentage = percentage;
        this.maxDiscountAmount = maxDiscountAmount;
    }
    @Override
    public double calculateDiscount(Order order) {
        if(order == null||order.getItems()==null) return 0.0;
        
        double subTotal =0;
        for(OrderItem item:order.getItems()){
            subTotal += item.calculateItemPrice();
        }
        double calculatedDiscount = subTotal *percentage;
        if(calculatedDiscount > maxDiscountAmount){
            return maxDiscountAmount;
        }
        return calculatedDiscount;
    }
    
}
