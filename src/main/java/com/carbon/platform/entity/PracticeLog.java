package com.carbon.platform.entity;

import com.carbon.platform.enums.CropCategory;
import com.carbon.platform.enums.GrowingSeason;
import com.carbon.platform.enums.PracticeStatus;
import com.carbon.platform.enums.PracticeType;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "practice_logs")
public class PracticeLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "farmer_id")
    private Farmer farmer;

    @ManyToOne
    @JoinColumn(name = "parcel_id")
    private LandParcel parcel;

    @Enumerated(EnumType.STRING)
    private PracticeType practiceType;

    @Enumerated(EnumType.STRING)
    private CropCategory cropCategory;

    @Enumerated(EnumType.STRING)
    private GrowingSeason growingSeason;

    private Double quantity;
    private LocalDate sowingDate;
    private LocalDate harvestingDate;

    @Enumerated(EnumType.STRING)
    private PracticeStatus status; // PENDING default

    private String adminComments;
    private int submissionCount = 1; // increments on resubmission

    @Lob
    @Column(columnDefinition = "CLOB")
    private String proofDocument; // Base64 or local filepath

    @CreationTimestamp
    private LocalDateTime createdAt;

    // Constructors
    public PracticeLog() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Farmer getFarmer() { return farmer; }
    public void setFarmer(Farmer farmer) { this.farmer = farmer; }

    public LandParcel getParcel() { return parcel; }
    public void setParcel(LandParcel parcel) { this.parcel = parcel; }

    public PracticeType getPracticeType() { return practiceType; }
    public void setPracticeType(PracticeType practiceType) { this.practiceType = practiceType; }

    public CropCategory getCropCategory() { return cropCategory; }
    public void setCropCategory(CropCategory cropCategory) { this.cropCategory = cropCategory; }

    public GrowingSeason getGrowingSeason() { return growingSeason; }
    public void setGrowingSeason(GrowingSeason growingSeason) { this.growingSeason = growingSeason; }

    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }

    public LocalDate getSowingDate() { return sowingDate; }
    public void setSowingDate(LocalDate sowingDate) { this.sowingDate = sowingDate; }

    public LocalDate getHarvestingDate() { return harvestingDate; }
    public void setHarvestingDate(LocalDate harvestingDate) { this.harvestingDate = harvestingDate; }

    public PracticeStatus getStatus() { return status; }
    public void setStatus(PracticeStatus status) { this.status = status; }

    public String getAdminComments() { return adminComments; }
    public void setAdminComments(String adminComments) { this.adminComments = adminComments; }

    public int getSubmissionCount() { return submissionCount; }
    public void setSubmissionCount(int submissionCount) { this.submissionCount = submissionCount; }

    public String getProofDocument() { return proofDocument; }
    public void setProofDocument(String proofDocument) { this.proofDocument = proofDocument; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    @Transient
    private Double calculatedCredits;

    public Double getCalculatedCredits() { return calculatedCredits; }
    public void setCalculatedCredits(Double calculatedCredits) { this.calculatedCredits = calculatedCredits; }
}
