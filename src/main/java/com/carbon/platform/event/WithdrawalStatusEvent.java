package com.carbon.platform.event;

public class WithdrawalStatusEvent {
    private final Long userId;
    private final Double amount;
    private final String status;
    private final String comments;

    public WithdrawalStatusEvent(Long userId, Double amount, String status, String comments) {
        this.userId = userId;
        this.amount = amount;
        this.status = status;
        this.comments = comments;
    }

    public Long getUserId() { return userId; }
    public Double getAmount() { return amount; }
    public String getStatus() { return status; }
    public String getComments() { return comments; }
}
