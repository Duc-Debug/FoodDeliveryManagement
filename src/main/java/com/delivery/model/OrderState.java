package com.delivery.model;

public enum OrderState {
    CREATED,      
    PREPARING,    
    DELIVERED,    
    CANCELLED;    

    public boolean canTransitionTo(OrderState nextState) {
        return switch (this) {
            case CREATED -> nextState == PREPARING || nextState == CANCELLED;
            case PREPARING -> nextState == DELIVERED;
            case DELIVERED, CANCELLED -> false;
        };
    }
}