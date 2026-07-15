package com.delivery.model;

import com.delivery.exception.ValidationException;

/**
 * Thực thể OrderItem đại diện cho một dòng hàng trong giỏ hàng hoặc đơn hàng.
 */
public class OrderItem {
    private MenuItem menuItem;
    private int quantity;

    public OrderItem(MenuItem menuItem, int qty) {
        if (qty <= 0) {
            throw new ValidationException("The order quantity must be greater than 0!", "INVALID_INPUT");
        }
        this.menuItem = menuItem;
        this.quantity = qty;
    }

    public OrderItem() {
    }

    public double calculateItemPrice() {
        if (menuItem == null)
            return 0.0;
        return menuItem.getBasePrice() * quantity;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int newQuantity) {
        if (newQuantity <= 0) {
            throw new ValidationException("The order quantity must be greater than 0!", "INVALID_INPUT");
        }
        this.quantity = newQuantity;
    }
}