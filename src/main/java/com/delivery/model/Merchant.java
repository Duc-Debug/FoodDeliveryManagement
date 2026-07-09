package com.delivery.model;

import com.delivery.exception.ValidationException;

/**
 * Thực thể Merchant đại diện cho đối tác cửa hàng ẩm thực.
 */
public class Merchant extends User {
    private String storeName;
    private double averageRating;
    private int totalReviews;

    public Merchant(String id, String name, String phoneNumber, String storeName) {
        super(id, name, phoneNumber);
        this.storeName = storeName;
        this.averageRating = 5.0;
        this.totalReviews = 0;
    }

    public Merchant(String id, String name, String phoneNumber, double initialBalance, String storeName, double averageRating, int totalReviews) {
        super(id, name, phoneNumber, initialBalance); 
        this.storeName = storeName;
        this.averageRating = averageRating;
        this.totalReviews = totalReviews;
    }

    public void updateAverageRating(int newRating) {
        if (newRating < 1 || newRating > 5) {
            throw new ValidationException("Số sao đánh giá cửa hàng phải nằm trong khoảng từ 1 đến 5!", "INVALID_RATING");
        }
        
        double totalScore = (this.averageRating * this.totalReviews) + newRating;
        this.totalReviews++;
        this.averageRating = totalScore / this.totalReviews;
    }

    @Override
    public String getDetails() {
        return String.format("[CỬA HÀNG] ID: %s | Tên quán: %s | Chủ tiệm: %s | SĐT: %s | Đánh giá: %.1f sao (%d lượt) | Số dư ví: %,.0f VNĐ",
                getId(), storeName, getName(), getPhoneNumber(), averageRating, totalReviews, getBalance());
    }

    public String getStoreName() {
        return storeName;
    }

    public void setStoreName(String storeName) {
        this.storeName = storeName;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public int getTotalReviews() {
        return totalReviews;
    }
}