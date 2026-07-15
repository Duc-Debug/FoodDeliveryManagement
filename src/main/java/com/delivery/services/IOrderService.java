package com.delivery.services;

import java.util.List;

import com.delivery.model.Cart;
import com.delivery.model.Order;
import com.delivery.model.OrderState;
import com.delivery.services.strategy.IDiscountStrategy;

public interface IOrderService {
    void createOrder(String orderId, String customerId, Cart cart, double shippingFee);

    Order checkout(Order order, IDiscountStrategy discountStrategy);

    List<Order> getAllOrders();

    void updateStatus(String orderId, OrderState newState);

    Order getOrderById(String orderId);

    void submitReview(String orderId, int rating, String comment);
}