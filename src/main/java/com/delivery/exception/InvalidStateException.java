package com.delivery.exception;
//Dùng cho lỗi nhảy cóc trạng thái đơn hàng hoặc hủy đơn sai lúc.
public class InvalidStateException extends BaseDeliveryException {
    public InvalidStateException(String message) {
        super(message, "INVALID_STATE");
    }
    public InvalidStateException(String message,String errorCode){
        super(message, errorCode);
    }
}