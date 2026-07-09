package com.delivery.exception;
//Dùng chung cho trùng ID, giá âm, ví thiếu tiền, số sao đánh giá sai.
public class ValidationException extends BaseDeliveryException {
    public ValidationException(String message) {
        super(message, "VALIDATION_ERROR");
    }
    public ValidationException(String message,String errorCode){
        super(message, errorCode);
    }
}

