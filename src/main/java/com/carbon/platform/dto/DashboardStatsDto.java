package com.carbon.platform.dto;

public class DashboardStatsDto {
    private String farmerEmail;
    private long totalAcres;
    private long practiceCount;
    private double totalCredits;
    private double walletBalance;

    public DashboardStatsDto() {}

    public DashboardStatsDto(String farmerEmail, long totalAcres, long practiceCount, double totalCredits, double walletBalance) {
        this.farmerEmail = farmerEmail;
        this.totalAcres = totalAcres;
        this.practiceCount = practiceCount;
        this.totalCredits = totalCredits;
        this.walletBalance = walletBalance;
    }

    public String getFarmerEmail() { return farmerEmail; }
    public void setFarmerEmail(String farmerEmail) { this.farmerEmail = farmerEmail; }
    public long getTotalAcres() { return totalAcres; }
    public void setTotalAcres(long totalAcres) { this.totalAcres = totalAcres; }
    public long getPracticeCount() { return practiceCount; }
    public void setPracticeCount(long practiceCount) { this.practiceCount = practiceCount; }
    public double getTotalCredits() { return totalCredits; }
    public void setTotalCredits(double totalCredits) { this.totalCredits = totalCredits; }
    public double getWalletBalance() { return walletBalance; }
    public void setWalletBalance(double walletBalance) { this.walletBalance = walletBalance; }
}
