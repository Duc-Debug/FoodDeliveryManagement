package com.delivery.model;

import com.delivery.exception.ValidationException;

/**
 * Thực thể cụ thể MenuItem đại diện cho một món ăn/đồ uống phẳng trong hệ thống.
 */
public class MenuItem {
    private String id;
    private String merchantId; 
    private String name;
    private double basePrice;
    private String description;

    public MenuItem(String id, String merchantId, String name, double basePrice, String description) {
        if (basePrice <= 0) {
            throw new ValidationException("Giá bán gốc của món ăn phải lớn hơn 0!", "INVALID_INPUT");
        }
        this.id = id;
        this.merchantId = merchantId;
        this.name = name;
        this.basePrice = basePrice;
        this.description = description;
    }

    public MenuItem() {
    }

    public String getDetailDescription() {
        return String.format("[%s] %s - Giá: %,.0f VNĐ | %s", id, name, basePrice, description);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMerchantId() {
        return merchantId;
    }

    public void setMerchantId(String merchantId) {
        this.merchantId = merchantId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(double basePrice) {
        if (basePrice <= 0) {
            throw new ValidationException("Giá bán gốc của món ăn phải lớn hơn 0!", "INVALID_INPUT");
        }
        this.basePrice = basePrice;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}