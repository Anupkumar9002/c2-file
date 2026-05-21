package com.carbon.platform.entity;

import com.carbon.platform.enums.ListingStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "listings")
public class Listing {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "farmer_id")
    private Farmer farmer;

    @ManyToOne
    @JoinColumn(name = "credit_id")
    private CarbonCredit carbonCredit;

    private Double quantity;
    private Double pricePerCredit;
    private LocalDate validUntil;

    @Enumerated(EnumType.STRING)
    private ListingStatus status; // ACTIVE default

    @CreationTimestamp
    private LocalDateTime listedAt;

    // Constructors
    public Listing() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Farmer getFarmer() { return farmer; }
    public void setFarmer(Farmer farmer) { this.farmer = farmer; }

    public CarbonCredit getCarbonCredit() { return carbonCredit; }
    public void setCarbonCredit(CarbonCredit carbonCredit) { this.carbonCredit = carbonCredit; }

    public Double getQuantity() { return quantity; }
    public void setQuantity(Double quantity) { this.quantity = quantity; }

    public Double getPricePerCredit() { return pricePerCredit; }
    public void setPricePerCredit(Double pricePerCredit) { this.pricePerCredit = pricePerCredit; }

    public LocalDate getValidUntil() { return validUntil; }
    public void setValidUntil(LocalDate validUntil) { this.validUntil = validUntil; }

    public ListingStatus getStatus() { return status; }
    public void setStatus(ListingStatus status) { this.status = status; }

    public LocalDateTime getListedAt() { return listedAt; }
    public void setListedAt(LocalDateTime listedAt) { this.listedAt = listedAt; }
}
