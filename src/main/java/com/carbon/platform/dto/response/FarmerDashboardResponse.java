package com.carbon.platform.dto.response;

public class FarmerDashboardResponse {
    private Double walletBalance;
    private Double totalAreaAcres;
    private Double totalCreditsEarned;
    private Double totalCreditsSold;
    private int parcelCount;
    private int practiceLogCount;

    // Constructors
    public FarmerDashboardResponse() {}

    public FarmerDashboardResponse(Double walletBalance, Double totalAreaAcres, Double totalCreditsEarned, Double totalCreditsSold, int parcelCount, int practiceLogCount) {
        this.walletBalance = walletBalance;
        this.totalAreaAcres = totalAreaAcres;
        this.totalCreditsEarned = totalCreditsEarned;
        this.totalCreditsSold = totalCreditsSold;
        this.parcelCount = parcelCount;
        this.practiceLogCount = practiceLogCount;
    }

    // Getters and Setters
    public Double getWalletBalance() { return walletBalance; }
    public void setWalletBalance(Double walletBalance) { this.walletBalance = walletBalance; }

    public Double getTotalAreaAcres() { return totalAreaAcres; }
    public void setTotalAreaAcres(Double totalAreaAcres) { this.totalAreaAcres = totalAreaAcres; }

    public Double getTotalCreditsEarned() { return totalCreditsEarned; }
    public void setTotalCreditsEarned(Double totalCreditsEarned) { this.totalCreditsEarned = totalCreditsEarned; }

    public Double getTotalCreditsSold() { return totalCreditsSold; }
    public void setTotalCreditsSold(Double totalCreditsSold) { this.totalCreditsSold = totalCreditsSold; }

    public int getParcelCount() { return parcelCount; }
    public void setParcelCount(int parcelCount) { this.parcelCount = parcelCount; }

    public int getPracticeLogCount() { return practiceLogCount; }
    public void setPracticeLogCount(int practiceLogCount) { this.practiceLogCount = practiceLogCount; }
}
