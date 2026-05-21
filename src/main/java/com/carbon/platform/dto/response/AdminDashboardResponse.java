package com.carbon.platform.dto.response;

public class AdminDashboardResponse {
    private long totalFarmers;
    private long totalCompanies;
    private long pendingFarmerVerifications;
    private long pendingCompanyVerifications;
    private long pendingPracticeLogs;
    private long totalTransactions;
    private Double totalPlatformEarnings;

    // Constructors
    public AdminDashboardResponse() {}

    public AdminDashboardResponse(long totalFarmers, long totalCompanies, long pendingFarmerVerifications,
                                  long pendingCompanyVerifications, long pendingPracticeLogs,
                                  long totalTransactions, Double totalPlatformEarnings) {
        this.totalFarmers = totalFarmers;
        this.totalCompanies = totalCompanies;
        this.pendingFarmerVerifications = pendingFarmerVerifications;
        this.pendingCompanyVerifications = pendingCompanyVerifications;
        this.pendingPracticeLogs = pendingPracticeLogs;
        this.totalTransactions = totalTransactions;
        this.totalPlatformEarnings = totalPlatformEarnings;
    }

    // Getters and Setters
    public long getTotalFarmers() { return totalFarmers; }
    public void setTotalFarmers(long totalFarmers) { this.totalFarmers = totalFarmers; }

    public long getTotalCompanies() { return totalCompanies; }
    public void setTotalCompanies(long totalCompanies) { this.totalCompanies = totalCompanies; }

    public long getPendingFarmerVerifications() { return pendingFarmerVerifications; }
    public void setPendingFarmerVerifications(long pendingFarmerVerifications) { this.pendingFarmerVerifications = pendingFarmerVerifications; }

    public long getPendingCompanyVerifications() { return pendingCompanyVerifications; }
    public void setPendingCompanyVerifications(long pendingCompanyVerifications) { this.pendingCompanyVerifications = pendingCompanyVerifications; }

    public long getPendingPracticeLogs() { return pendingPracticeLogs; }
    public void setPendingPracticeLogs(long pendingPracticeLogs) { this.pendingPracticeLogs = pendingPracticeLogs; }

    public long getTotalTransactions() { return totalTransactions; }
    public void setTotalTransactions(long totalTransactions) { this.totalTransactions = totalTransactions; }

    public Double getTotalPlatformEarnings() { return totalPlatformEarnings; }
    public void setTotalPlatformEarnings(Double totalPlatformEarnings) { this.totalPlatformEarnings = totalPlatformEarnings; }
}
