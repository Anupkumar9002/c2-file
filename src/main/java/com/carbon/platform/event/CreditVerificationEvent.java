package com.carbon.platform.event;

public class CreditVerificationEvent {
    private final Long userId;
    private final Double quantity;

    public CreditVerificationEvent(Long userId, Double quantity) {
        this.userId = userId;
        this.quantity = quantity;
    }

    public Long getUserId() { return userId; }
    public Double getQuantity() { return quantity; }
}
