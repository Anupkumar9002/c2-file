package com.carbon.platform.entity;

import com.carbon.platform.enums.CropCategory;
import com.carbon.platform.enums.GrowingSeason;
import com.carbon.platform.enums.PracticeType;
import jakarta.persistence.*;

@Entity
@Table(name = "credit_formulas")
public class CreditFormula {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(unique = true, nullable = false)
    private PracticeType practiceType;

    private Double baseCoefficient = 1.0;

    private Double kharifMultiplier = 1.0;
    private Double rabiMultiplier = 1.0;
    private Double zaidMultiplier = 1.0;

    private Double foodCropMultiplier = 1.0;
    private Double cashCropMultiplier = 1.0;
    private Double plantationCropMultiplier = 1.0;
    private Double horticultureCropMultiplier = 1.0;

    private Double maxCap = 100.0;
    private String version = "1.0";
    private boolean active = true;

    // Constructors
    public CreditFormula() {}

    public CreditFormula(PracticeType practiceType, Double baseCoefficient, Double maxCap, String version) {
        this.practiceType = practiceType;
        this.baseCoefficient = baseCoefficient;
        this.maxCap = maxCap;
        this.version = version;
        this.active = true;
    }

    // Business Methods
    public double getSeasonMultiplier(GrowingSeason season) {
        if (season == null) return 1.0;
        return switch (season) {
            case KHARIF -> kharifMultiplier != null ? kharifMultiplier : 1.0;
            case RABI -> rabiMultiplier != null ? rabiMultiplier : 1.0;
            case ZAID -> zaidMultiplier != null ? zaidMultiplier : 1.0;
        };
    }

    public double getCropCategoryMultiplier(CropCategory category) {
        if (category == null) return 1.0;
        return switch (category) {
            case FOOD -> foodCropMultiplier != null ? foodCropMultiplier : 1.0;
            case CASH -> cashCropMultiplier != null ? cashCropMultiplier : 1.0;
            case PLANTATION -> plantationCropMultiplier != null ? plantationCropMultiplier : 1.0;
            case HORTICULTURE -> horticultureCropMultiplier != null ? horticultureCropMultiplier : 1.0;
        };
    }

    public double getDurationFactor(long days) {
        // Example logic: longer growth duration adds a small reward factor (e.g. 0.001 per day beyond 90 days)
        if (days <= 0) return 1.0;
        if (days < 90) return 0.9;
        if (days <= 180) return 1.0;
        return 1.1; // > 180 days gets 10% boost
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PracticeType getPracticeType() { return practiceType; }
    public void setPracticeType(PracticeType practiceType) { this.practiceType = practiceType; }

    public Double getBaseCoefficient() { return baseCoefficient; }
    public void setBaseCoefficient(Double baseCoefficient) { this.baseCoefficient = baseCoefficient; }

    public Double getKharifMultiplier() { return kharifMultiplier; }
    public void setKharifMultiplier(Double kharifMultiplier) { this.kharifMultiplier = kharifMultiplier; }

    public Double getRabiMultiplier() { return rabiMultiplier; }
    public void setRabiMultiplier(Double rabiMultiplier) { this.rabiMultiplier = rabiMultiplier; }

    public Double getZaidMultiplier() { return zaidMultiplier; }
    public void setZaidMultiplier(Double zaidMultiplier) { this.zaidMultiplier = zaidMultiplier; }

    public Double getFoodCropMultiplier() { return foodCropMultiplier; }
    public void setFoodCropMultiplier(Double foodCropMultiplier) { this.foodCropMultiplier = foodCropMultiplier; }

    public Double getCashCropMultiplier() { return cashCropMultiplier; }
    public void setCashCropMultiplier(Double cashCropMultiplier) { this.cashCropMultiplier = cashCropMultiplier; }

    public Double getPlantationCropMultiplier() { return plantationCropMultiplier; }
    public void setPlantationCropMultiplier(Double plantationCropMultiplier) { this.plantationCropMultiplier = plantationCropMultiplier; }

    public Double getHorticultureCropMultiplier() { return horticultureCropMultiplier; }
    public void setHorticultureCropMultiplier(Double horticultureCropMultiplier) { this.horticultureCropMultiplier = horticultureCropMultiplier; }

    public Double getMaxCap() { return maxCap; }
    public void setMaxCap(Double maxCap) { this.maxCap = maxCap; }

    public String getVersion() { return version; }
    public void setVersion(String version) { this.version = version; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
}
