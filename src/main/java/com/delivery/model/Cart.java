package com.delivery.model;

import com.delivery.exception.NotFoundException;
import com.delivery.exception.ValidationException;
import java.util.ArrayList;
import java.util.List;

/**
 * Thực thể Giỏ hàng đại diện cho vùng lưu trữ nháp tạm thời trong RAM của Khách
 * hàng.
 */
public class Cart {
    private final List<OrderItem> items = new ArrayList<>();

    public List<OrderItem> getItems() {
        return List.copyOf(items);
    }

    public void addItem(MenuItem item, int quantity) {
        if (quantity <= 0) {
            throw new ValidationException("The quantity added to the cart must be greater than 0!");
        }

        OrderItem existingItem = null;
        for (OrderItem orderItem : items) {
            if (orderItem.getMenuItem().getId().equals(item.getId())) {
                existingItem = orderItem;
                break;
            }
        }

        if (existingItem != null) {
            int predictQty = existingItem.getQuantity() + quantity;
            if (predictQty > 50) {
                throw new ValidationException(
                        String.format("Cannot add! '%s' already has %d items in the cart, the maximum limit is 50.",
                                item.getName(), existingItem.getQuantity()),
                        "EXCEEDED_LIMIT");
            }
            existingItem.setQuantity(predictQty);
        } else {
            if (quantity > 50) {
                throw new ValidationException(
                        "Exceeded the maximum allowed order quantity per item (Maximum 50 portions)!",
                        "EXCEEDED_LIMIT");
            }
            items.add(new OrderItem(item, quantity));
        }
    }

    public void removeItem(String menuItemId) {
        boolean removed = items.removeIf(item -> item.getMenuItem().getId().equals(menuItemId));
        if (!removed) {
            throw new NotFoundException("This food item was not found in your current cart!");
        }
    }

    public double calculateSubTotal() {
        double sum = 0;
        for (OrderItem item : items) {
            sum += item.calculateItemPrice();
        }
        return sum;
    }

    public int getTotalQuantity() {
        int total = 0;
        for (OrderItem item : items) {
            total += item.getQuantity();
        }
        return total;
    }

    public void clearCart() {
        items.clear();
    }
}