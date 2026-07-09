package com.delivery.model;
import com.delivery.exception.ValidationException;;
/**
 * Lớp cha trừu tượng User đại diện cho định danh chung của mọi tài khoản trong hệ thống.
 */
public abstract class User {
    private String id;
    private String name;
    private String phoneNumber;
    protected double balance; 

    public User(String id, String name, String phoneNumber) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.balance = 0.0;
    }
    public User(String id, String name, String phoneNumber, double initialBalance) {
        this.id = id;
        this.name = name;
        this.phoneNumber = phoneNumber;
        this.balance = initialBalance;
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

    public abstract String getDetails();

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
}