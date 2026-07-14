package com.delivery.model;

import com.delivery.exception.InvalidStateException;
import com.delivery.exception.ValidationException;
import java.util.List;

/**
 * Thực thể trung tâm Order quản lý toàn bộ vòng đời của một đơn hàng, đóng gói logic tính tiền và hệ thống đánh giá tích hợp trực tiếp.
 */
public class Order {
    private String orderId;
    private User customer;
    private List<OrderItem> items; 
    private OrderState state;
    private double shippingFee;
    private double discount;
    private double totalPrice;
    private int rating;            
    private String comment;      

    public Order(String orderId, User customer, List<OrderItem> items, double shippingFee, double discount) {
        this.orderId = orderId;
        this.customer = customer;
        this.items = items;
        this.state = OrderState.CREATED; 
        this.shippingFee = shippingFee;
        this.discount = discount;
        this.rating = 0;                
        this.comment = "";
        calculateTotalPrice();          
    }

    public Order(String orderId, User customer, List<OrderItem> items, OrderState state, double shippingFee, double discount, double totalPrice, int rating, String comment) {
        this.orderId = orderId;
        this.customer = customer;
        this.items = items;
        this.state = state;
        this.shippingFee = shippingFee;
        this.discount = discount;
        this.totalPrice = totalPrice;
        this.rating = rating;
        this.comment = comment;
    }

    public void calculateTotalPrice() {
        double subTotal = 0;
        for (OrderItem item : items) {
            subTotal += item.calculateItemPrice();
        }
        // Tổng tiền = Tiền hàng + Phí ship - Giảm giá
        this.totalPrice = subTotal + shippingFee - discount;
        if (this.totalPrice < 0) {
            this.totalPrice = 0;
        }
    }

    public void updateState(OrderState newState) {
        if (!this.state.canTransitionTo(newState)) {
            throw new InvalidStateException("Chuyển trạng thái đơn hàng sai quy trình vận hành");
        }
        this.state = newState;
    }

    public void cancelOrder() {
        if (this.state != OrderState.CREATED) {
            throw new InvalidStateException("Cửa hàng đang nấu, không thể hủy bỏ đơn hàng này!", "CANCEL_REJECTED");
        }
        this.state = OrderState.CANCELLED;
    }

    public void submitReview(int rating, String comment) {
        if (this.state != OrderState.DELIVERED) {
            throw new InvalidStateException("Đơn hàng chưa hoàn thành giao, không thể thực hiện đánh giá!");
        }
        if (rating < 1 || rating > 5) {
            throw new ValidationException("Số sao chấm đánh giá bắt buộc phải nằm trong khoảng từ 1 đến 5!", "INVALID_INPUT");
        }
        this.rating = rating;
        this.comment = comment;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void setItems(List<OrderItem> items) {
        this.items = items;
        calculateTotalPrice();
    }

    public OrderState getState() {
        return state;
    }

    public double getShippingFee() {
        return shippingFee;
    }

    public void setShippingFee(double shippingFee) {
        this.shippingFee = shippingFee;
        calculateTotalPrice();
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
        calculateTotalPrice();
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public int getRating() {
        return rating;
    }

    public String getComment() {
        return comment;
    }

    public User getCustomer() {
        return customer;
    }

    public void setCustomer(User customer) {
        this.customer = customer;
    }
}