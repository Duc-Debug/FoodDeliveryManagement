package com.delivery.util;

import java.util.ArrayList;
import java.util.List;

import com.delivery.model.*;
public final class CsvMapper{
    private CsvMapper() {}
    public static String toCsvRow(User user) {
        if (user instanceof Customer c) {
            return String.join(",", "CUSTOMER", 
                csvEscape(c.getId()), 
                csvEscape(c.getName()),
                csvEscape(c.getPhoneNumber()), 
                String.valueOf(c.getBalance()), 
                csvEscape(c.getAddress())
            );
        }
        if (user instanceof Merchant m) {
            return String.join(",", "MERCHANT", 
                csvEscape(m.getId()), 
                csvEscape(m.getName()),
                csvEscape(m.getPhoneNumber()), 
                String.valueOf(m.getBalance()), 
                csvEscape(m.getStoreName()),
                String.valueOf(m.getAverageRating()), 
                String.valueOf(m.getTotalReviews())
            );
        }
        return "";
    }
    public static String toCsvRow(MenuItem item) {
        return String.join(",", "MENU_ITEM",
                csvEscape(item.getId()),
                csvEscape(item.getMerchantId()),
                csvEscape(item.getName()),
                String.valueOf((item.getBasePrice())),
                csvEscape(item.getDescription())
        );
    }
    public static String toCsvRow(Order order){
        List<String> itemTokens = new ArrayList<>();
        for (OrderItem item: order.getItems()){
            String token = item.getMenuItem().getId() + ":" +item.getQuantity();
            itemTokens.add(token);
        }
       String itemsCompressed = String.join("|", itemTokens);
       return String.join(",",
                order.getOrderId(),
                order.getCustomer().getId(),
                order.getMerchant().getId(),
                itemsCompressed, 
                String.valueOf(order.getShippingFee()),
                String.valueOf(order.getDiscount()),
                String.valueOf(order.getTotalPrice()),
                order.getState().name(),
                String.valueOf(order.getRating()),
                order.getComment().isBlank() ? "NONE" : order.getComment()
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
