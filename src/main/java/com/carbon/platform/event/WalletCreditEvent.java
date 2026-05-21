package com.carbon.platform.event;

public class WalletCreditEvent {
    private final Long userId;
    private final Double amount;
    private final Double balance;

    public WalletCreditEvent(Long userId, Double amount, Double balance) {
        this.userId = userId;
        this.amount = amount;
        this.balance = balance;
    }

    public Long getUserId() { return userId; }
    public Double getAmount() { return amount; }
    public Double getBalance() { return balance; }
}
