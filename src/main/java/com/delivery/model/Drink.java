package com.delivery.model;
/*
Lớp con Food kế thừa từ lớp cha MenuItem thể hiện các đồ uống.
 */
public class Drink extends MenuItem {
private String size;
private int defaultSurgarLevel; // Vd 10/ 50/70/100
private int defaultIceLevel;
public Drink(String id, String name, double basePrice, String description,String size, int defaultSurgarLevel, int defaultIceLevel) {
    super(id, name, basePrice, description);
    this.size = size;
    this.defaultIceLevel = defaultIceLevel;
    this.defaultSurgarLevel = defaultSurgarLevel;
}
    @Override
    public String getDetailDescription() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getDetailDescription'");
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
