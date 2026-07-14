package com.delivery.model;

import com.delivery.exception.ValidationException;

/**
 * Lớp cha trừu tượng MenuItem đại diện cho định danh chung của mọi loại món ăn trong hệ thống.
 */
public abstract class MenuItem {
    private String id;
    private String name;
    private double basePrice;
    private String description;

    public MenuItem(String id, String name, double basePrice, String description) {
        if (basePrice <= 0) {
            throw new ValidationException("Giá bán gốc của món ăn phải lớn hơn 0!", "INVALID_INPUT");
        }
        this.id = id;
        this.name = name;
        this.basePrice = basePrice;
        this.description = description;
    }

    public MenuItem() {
    }

    public abstract String getDetailDescription();

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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