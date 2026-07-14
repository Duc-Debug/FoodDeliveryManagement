package com.delivery.model;
import com.delivery.exception.ValidationException;;
/**
 * Thực thể User đại diện cho định danh chung của các khách hàng trong hệ thống.
 */
public abstract class User {
    private String id;
    private String name;
    private String phoneNumber;
    protected double balance; 

     private String address;
    private Cart cart; 
    public User(String id, String name, String phoneNumber,String address) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.balance = 0.0;
         this.address = address;
        this.cart = new Cart();
    }
    public User(String id, String name, String phoneNumber, double initialBalance,String address) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.balance = initialBalance;
         this.address = address;
        this.cart = new Cart();
    }

    public double getBalance() {
        return balance;
    }

    public void deposit(double amount) {
        if (amount <= 0) {
            throw new ValidationException("Số tiền nạp vào tài khoản phải lớn hơn 0!");
        }
        this.balance += amount;
    }
    public void deduct(double amount) {
        if (amount <= 0) {
            throw new ValidationException("Số tiền trừ khỏi tài khoản phải lớn hơn 0!");
        }
        if (this.balance < amount) {
            throw new ValidationException("Số dư tài khoản không đủ để thực hiện giao dịch!");
        }
        this.balance -= amount;
    }

   public String getDetails() {
        return String.format("[KHÁCH HÀNG] ID: %s | Tên: %s | SĐT: %s | Địa chỉ: %s | Số dư ví: %,.0f VNĐ",
                getId(), getName(), getPhoneNumber(), address, getBalance());
    }

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

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
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