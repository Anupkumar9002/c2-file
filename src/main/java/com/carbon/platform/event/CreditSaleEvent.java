package com.carbon.platform.event;

public class CreditSaleEvent {
    private final Long farmerUserId;
    private final Long companyUserId;
    private final Double quantity;
    private final Double totalAmount;
    private final Double netAmount;

    public CreditSaleEvent(Long farmerUserId, Long companyUserId, Double quantity, Double totalAmount, Double netAmount) {
        this.farmerUserId = farmerUserId;
        this.companyUserId = companyUserId;
        this.quantity = quantity;
        this.totalAmount = totalAmount;
        this.netAmount = netAmount;
    }

    public Long getFarmerUserId() { return farmerUserId; }
    public Long getCompanyUserId() { return companyUserId; }
    public Double getQuantity() { return quantity; }
    public Double getTotalAmount() { return totalAmount; }
    public Double getNetAmount() { return netAmount; }
}
