package com.delivery.exception;

public abstract class BaseDeliveryException extends RuntimeException {
    private final String errorCode;
    public BaseDeliveryException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
    
    public String getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return String.format("[ERROR] [%s] %s",
         errorCode,
         getMessage());
    }
}
