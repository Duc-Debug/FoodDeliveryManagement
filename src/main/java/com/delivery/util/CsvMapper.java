package com.delivery.util;

import java.util.ArrayList;
import java.util.List;

import com.delivery.model.*;
/**
 * Lớp tiện ích chịu trách nhiệm chuyển đổi (Mapping) các đối tượng RAM sang chuỗi CSV phẳng.
 * Gom toàn bộ logic xử lý chuỗi bẩn về một mối.
 */
public final class CsvMapper{
    private CsvMapper() {}
    public static String toCsvRow(User user) {
            return String.join(",", 
                csvEscape(user.getId()), 
                csvEscape(user.getName()),
                csvEscape(user.getPhoneNumber()), 
                String.valueOf(user.getBalance()), 
                csvEscape(user.getAddress())
            );
    }
    public static String toCsvRow(MenuItem item) {
        if (item == null) return "";

        // Tình huống 1: Mặt hàng là Đồ ăn
        if (item instanceof Food f) {
            return String.join(",", 
                "FOOD", // Cờ hiệu nhận diện loại ở đầu dòng
                csvEscape(f.getId()), 
                csvEscape(f.getName()),
                String.valueOf(f.getBasePrice()), 
                csvEscape(f.getDescription()), 
                csvEscape(f.getPortionSize()), 
                String.valueOf(f.isVegetarian())
            );
        }

        // Tình huống 2: Mặt hàng là Đồ uống
        if (item instanceof Drink d) {
            return String.join(",", 
                "DRINK",
                csvEscape(d.getId()), 
                csvEscape(d.getName()),
                String.valueOf(d.getBasePrice()), 
                csvEscape(d.getDescription()), 
                csvEscape(d.getSize()), 
                String.valueOf(d.getDefaultSurgarLevel()), 
                String.valueOf(d.getDefaultIceLevel())
            );
        }

        return "";
    }
    public static String toCsvRow(Order order){
        if(order ==null) return"";
        List<String> itemTokens = new ArrayList<>();
        for (OrderItem item: order.getItems()){
            String token = item.getMenuItem().getId() + ":" +item.getQuantity();
            itemTokens.add(token);
        }
       String itemsCompressed = String.join("|", itemTokens);
       return String.join(",",
                order.getOrderId(),
                order.getCustomerId(),
                itemsCompressed, 
                String.valueOf(order.getShippingFee()),
                String.valueOf(order.getDiscount()),
                String.valueOf(order.getTotalPrice()),
                order.getState().name(),
                String.valueOf(order.getRating()),
                order.getComment().isBlank() ? "NONE" : order.getComment(),
                String.valueOf(order.isPaid())
        );
    }
    private static String csvEscape(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }
}
