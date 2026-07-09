package com.delivery.exception;
//Không tìm thấy món, user...
public class NotFoundException extends BaseDeliveryException {
    public NotFoundException(String message) {
        super(message, "NOT_FOUND");
    }
    
}