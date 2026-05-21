package com.carbon.platform.dto;

import java.time.LocalDateTime;

public class CarbonAssetDto {
    private Long practiceLogId;
    private Double computedVolume; // finalCredits
    private String formulaConfigured; // formulaVersion
    private String activeCapApplied; // "Capped" or "Uncapped"
    private LocalDateTime creationDate; // calculatedAt
    private String status; // CreditStatus name
    private String calculationDetails; // JSON details of calculation

    public CarbonAssetDto() {}

    public CarbonAssetDto(Long practiceLogId, Double computedVolume, String formulaConfigured, String activeCapApplied, LocalDateTime creationDate, String status, String calculationDetails) {
        this.practiceLogId = practiceLogId;
        this.computedVolume = computedVolume;
        this.formulaConfigured = formulaConfigured;
        this.activeCapApplied = activeCapApplied;
        this.creationDate = creationDate;
        this.status = status;
        this.calculationDetails = calculationDetails;
    }

    public Long getPracticeLogId() { return practiceLogId; }
    public void setPracticeLogId(Long practiceLogId) { this.practiceLogId = practiceLogId; }
    public Double getComputedVolume() { return computedVolume; }
    public void setComputedVolume(Double computedVolume) { this.computedVolume = computedVolume; }
    public String getFormulaConfigured() { return formulaConfigured; }
    public void setFormulaConfigured(String formulaConfigured) { this.formulaConfigured = formulaConfigured; }
    public String getActiveCapApplied() { return activeCapApplied; }
    public void setActiveCapApplied(String activeCapApplied) { this.activeCapApplied = activeCapApplied; }
    public LocalDateTime getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getCalculationDetails() { return calculationDetails; }
    public void setCalculationDetails(String calculationDetails) { this.calculationDetails = calculationDetails; }
}
