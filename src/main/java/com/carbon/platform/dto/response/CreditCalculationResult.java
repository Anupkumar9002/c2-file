package com.carbon.platform.dto.response;

public class CreditCalculationResult {
    private Double rawCredits;
    private Double finalCredits;
    private boolean wasCapped;
    private String capReason;
    private String formulaVersion;
    private String calculationDetails; // JSON formatted details

    // Constructors
    public CreditCalculationResult() {}

    public CreditCalculationResult(Double rawCredits, Double finalCredits, boolean wasCapped, String capReason, String formulaVersion, String calculationDetails) {
        this.rawCredits = rawCredits;
        this.finalCredits = finalCredits;
        this.wasCapped = wasCapped;
        this.capReason = capReason;
        this.formulaVersion = formulaVersion;
        this.calculationDetails = calculationDetails;
    }

    // Getters and Setters
    public Double getRawCredits() { return rawCredits; }
    public void setRawCredits(Double rawCredits) { this.rawCredits = rawCredits; }

    public Double getFinalCredits() { return finalCredits; }
    public void setFinalCredits(Double finalCredits) { this.finalCredits = finalCredits; }

    public boolean isWasCapped() { return wasCapped; }
    public void setWasCapped(boolean wasCapped) { this.wasCapped = wasCapped; }

    public String getCapReason() { return capReason; }
    public void setCapReason(String capReason) { this.capReason = capReason; }

    public String getFormulaVersion() { return formulaVersion; }
    public void setFormulaVersion(String formulaVersion) { this.formulaVersion = formulaVersion; }

    public String getCalculationDetails() { return calculationDetails; }
    public void setCalculationDetails(String calculationDetails) { this.calculationDetails = calculationDetails; }
}
