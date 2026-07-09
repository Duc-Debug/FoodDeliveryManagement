package com.delivery.model;

import com.delivery.exception.NotFoundException;
import com.delivery.exception.ValidationException;
import java.util.ArrayList;
import java.util.List;

/**
 * Thực thể Giỏ hàng đại diện cho vùng lưu trữ nháp tạm thời trong RAM của Khách hàng.
 */
public class Cart {
    private final List<OrderItem> items = new ArrayList<>(); 

    public List<OrderItem> getItems() {
        return List.copyOf(items);
    }

    public void addItem(MenuItem item, int quantity) {
        if (quantity <= 0) { 
            throw new ValidationException("Số lượng thêm vào giỏ hàng phải lớn hơn 0!");   
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
                    String.format("Không thể thêm! Món '%s' đã có %d phần trong giỏ, giới hạn tối đa là 50 phần.", 
                        item.getName(), existingItem.getQuantity()), 
                    "EXCEEDED_LIMIT"
                );
            }
            existingItem.setQuantity(predictQty);
        } else {
            if (quantity > 50) {
                throw new ValidationException("Vượt quá số lượng đặt mua giới hạn cho phép một lần (Tối đa 50 phần)!", "EXCEEDED_LIMIT");
            }
            items.add(new OrderItem(item, quantity));
        }
    }

    public void removeItem(String menuItemId) {
        boolean removed = items.removeIf(item -> item.getMenuItem().getId().equals(menuItemId));
        if (!removed) {
            throw new NotFoundException("Không tìm thấy món ăn này trong giỏ hàng hiện tại!");
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