package com.delivery.model;

/**
 * Thực thể Customer đại diện cho khách hàng đặt đồ ăn trên hệ thống.
 */
public class Customer extends User {
    private String address;
    private Cart cart; 

    public Customer(String id, String name, String phoneNumber, String address) {
        super(id, name, phoneNumber); 
        this.address = address;
        this.cart = new Cart(); 
    }

    public Customer(String id, String name, String phoneNumber, double initialBalance, String address) {
        super(id, name, phoneNumber, initialBalance); 
        this.address = address;
        this.cart = new Cart(); 
    }

    @Override
    public String getDetails() {
        return String.format("[KHÁCH HÀNG] ID: %s | Tên: %s | SĐT: %s | Địa chỉ: %s | Số dư ví: %,.0f VNĐ",
                getId(), getName(), getPhoneNumber(), address, getBalance());
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Cart getCart() {
        return cart;
    }
    public void setCart(Cart cart) {
        this.cart = cart;
    }
}