package com.delivery.model;

/*
Lớp con Food kế thừa từ lớp che MenuItem thể hiện các món ăn.
 */
public class Food extends MenuItem {
    private String portionSize; // Suất đơn, Suất 2 ng,...
    private boolean isVegetarian;

    public Food(String id, String name, double basePrice, String description, String portionSize,
            boolean isVegetarian) {
        super(id, name, basePrice, description);
        this.portionSize = portionSize;
        this.isVegetarian = isVegetarian;
    }

    @Override
    public String getDetailDescription() {
        return String.format("[%s] %s - Price: %,.0f VND | %s | %s | Vegetarian: %b",
                getId(), getName(), getBasePrice(), getDescription(), getPortionSize(), isVegetarian());
    }

    public String getPortionSize() {
        return portionSize;
    }

    public void setPortionSize(String portionSize) {
        this.portionSize = portionSize;
    }

    public boolean isVegetarian() {
        return isVegetarian;
    }

    public void setVegetarian(boolean isVegetarian) {
        this.isVegetarian = isVegetarian;
    }

}
