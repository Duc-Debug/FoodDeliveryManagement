package com.delivery.model;

/*
Lớp con Food kế thừa từ lớp cha MenuItem thể hiện các đồ uống.
 */
public class Drink extends MenuItem {
    private String size;
    private int defaultSurgarLevel; // Vd 10/ 50/70/100
    private int defaultIceLevel;

    public Drink(String id, String name, double basePrice, String description, String size, int defaultSurgarLevel,
            int defaultIceLevel) {
        super(id, name, basePrice, description);
        this.size = size;
        this.defaultSurgarLevel = defaultSurgarLevel;
        this.defaultIceLevel = defaultIceLevel;
    }

    @Override
    public String getDetailDescription() {
        return String.format("[%s] %s - Price: %,.0f VND | %s | Size: %s | Sugar: %03d%% | Ice: %03d%%",
                getId(), getName(), getBasePrice(), getDescription(), getSize(), getDefaultSurgarLevel(),
                getDefaultIceLevel());
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public int getDefaultSurgarLevel() {
        return defaultSurgarLevel;
    }

    public void setDefaultSurgarLevel(int defaultSurgarLevel) {
        this.defaultSurgarLevel = defaultSurgarLevel;
    }

    public int getDefaultIceLevel() {
        return defaultIceLevel;
    }

    public void setDefaultIceLevel(int defaultIceLevel) {
        this.defaultIceLevel = defaultIceLevel;
    }

}
