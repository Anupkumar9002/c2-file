package com.carbon.platform.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "farmer_wallets")
public class FarmerWallet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "farmer_id")
    private Farmer farmer;

    private Double balance = 0.0;
    private Double totalEarned = 0.0;
    private Double totalWithdrawn = 0.0;

    // Constructors
    public FarmerWallet() {}

    public FarmerWallet(Farmer farmer) {
        this.farmer = farmer;
        this.balance = 0.0;
        this.totalEarned = 0.0;
        this.totalWithdrawn = 0.0;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Farmer getFarmer() { return farmer; }
    public void setFarmer(Farmer farmer) { this.farmer = farmer; }

    public Double getBalance() { return balance; }
    public void setBalance(Double balance) { this.balance = balance; }

    public Double getTotalEarned() { return totalEarned; }
    public void setTotalEarned(Double totalEarned) { this.totalEarned = totalEarned; }

    public Double getTotalWithdrawn() { return totalWithdrawn; }
    public void setTotalWithdrawn(Double totalWithdrawn) { this.totalWithdrawn = totalWithdrawn; }
}
