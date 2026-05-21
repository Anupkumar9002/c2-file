package com.carbon.platform.entity;

import com.carbon.platform.enums.LandType;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "land_parcels")
public class LandParcel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "farmer_id")
    private Farmer farmer;

    private String parcelName;
    private Double areaInAcres;
    private String location;

    @Enumerated(EnumType.STRING)
    private LandType landType;

    private boolean adminApproved = false;

    @CreationTimestamp
    private LocalDateTime createdAt;

    // Constructors
    public LandParcel() {}

    public LandParcel(Farmer farmer, String parcelName, Double areaInAcres, String location, LandType landType) {
        this.farmer = farmer;
        this.parcelName = parcelName;
        this.areaInAcres = areaInAcres;
        this.location = location;
        this.landType = landType;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Farmer getFarmer() { return farmer; }
    public void setFarmer(Farmer farmer) { this.farmer = farmer; }

    public String getParcelName() { return parcelName; }
    public void setParcelName(String parcelName) { this.parcelName = parcelName; }

    public Double getAreaInAcres() { return areaInAcres; }
    public void setAreaInAcres(Double areaInAcres) { this.areaInAcres = areaInAcres; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public LandType getLandType() { return landType; }
    public void setLandType(LandType landType) { this.landType = landType; }

    public boolean isAdminApproved() { return adminApproved; }
    public void setAdminApproved(boolean adminApproved) { this.adminApproved = adminApproved; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
