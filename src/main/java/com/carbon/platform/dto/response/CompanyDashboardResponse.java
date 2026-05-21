package com.carbon.platform.dto.response;

public class CompanyDashboardResponse {
    private int certificateCount;
    private Double totalCo2Offset;
    private Double totalSpend;
    private int activeCartCount;

    // Constructors
    public CompanyDashboardResponse() {}

    public CompanyDashboardResponse(int certificateCount, Double totalCo2Offset, Double totalSpend, int activeCartCount) {
        this.certificateCount = certificateCount;
        this.totalCo2Offset = totalCo2Offset;
        this.totalSpend = totalSpend;
        this.activeCartCount = activeCartCount;
    }

    // Getters and Setters
    public int getCertificateCount() { return certificateCount; }
    public void setCertificateCount(int certificateCount) { this.certificateCount = certificateCount; }

    public Double getTotalCo2Offset() { return totalCo2Offset; }
    public void setTotalCo2Offset(Double totalCo2Offset) { this.totalCo2Offset = totalCo2Offset; }

    public Double getTotalSpend() { return totalSpend; }
    public void setTotalSpend(Double totalSpend) { this.totalSpend = totalSpend; }

    public int getActiveCartCount() { return activeCartCount; }
    public void setActiveCartCount(int activeCartCount) { this.activeCartCount = activeCartCount; }
}
