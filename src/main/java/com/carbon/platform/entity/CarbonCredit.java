package com.carbon.platform.entity;

import com.carbon.platform.enums.CreditStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "carbon_credits")
public class CarbonCredit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "practice_log_id")
    private PracticeLog practiceLog;

    @ManyToOne
    @JoinColumn(name = "farmer_id")
    private Farmer farmer;

    private Double rawCalculatedCredits;
    private Double cappedCredits;       // after cap logic (US-011)
    private Double finalCredits;

    private String formulaVersion;

    @Lob
    @Column(columnDefinition = "CLOB")
    private String calculationDetails;  // JSON string for transparency (US-033)

    private boolean wasCapped = false;
    private String capReason;

    @Enumerated(EnumType.STRING)
    private CreditStatus status;        // PENDING_VERIFICATION default

    @CreationTimestamp
    private LocalDateTime calculatedAt;

    // Constructors
    public CarbonCredit() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PracticeLog getPracticeLog() { return practiceLog; }
    public void setPracticeLog(PracticeLog practiceLog) { this.practiceLog = practiceLog; }

    public Farmer getFarmer() { return farmer; }
    public void setFarmer(Farmer farmer) { this.farmer = farmer; }

    public Double getRawCalculatedCredits() { return rawCalculatedCredits; }
    public void setRawCalculatedCredits(Double rawCalculatedCredits) { this.rawCalculatedCredits = rawCalculatedCredits; }

    public Double getCappedCredits() { return cappedCredits; }
    public void setCappedCredits(Double cappedCredits) { this.cappedCredits = cappedCredits; }

    public Double getFinalCredits() { return finalCredits; }
    public void setFinalCredits(Double finalCredits) { this.finalCredits = finalCredits; }

    public String getFormulaVersion() { return formulaVersion; }
    public void setFormulaVersion(String formulaVersion) { this.formulaVersion = formulaVersion; }

    public String getCalculationDetails() { return calculationDetails; }
    public void setCalculationDetails(String calculationDetails) { this.calculationDetails = calculationDetails; }

    public boolean isWasCapped() { return wasCapped; }
    public void setWasCapped(boolean wasCapped) { this.wasCapped = wasCapped; }

    public String getCapReason() { return capReason; }
    public void setCapReason(String capReason) { this.capReason = capReason; }

    public CreditStatus getStatus() { return status; }
    public void setStatus(CreditStatus status) { this.status = status; }

    public LocalDateTime getCalculatedAt() { return calculatedAt; }
    public void setCalculatedAt(LocalDateTime calculatedAt) { this.calculatedAt = calculatedAt; }
}
