package com.carbon.platform.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "otps")
public class OtpEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String target; // email or mobile

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private LocalDateTime expiry;

    private boolean used = false;

    // Constructors
    public OtpEntity() {}

    public OtpEntity(String target, String code, LocalDateTime expiry) {
        this.target = target;
        this.code = code;
        this.expiry = expiry;
        this.used = false;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public LocalDateTime getExpiry() { return expiry; }
    public void setExpiry(LocalDateTime expiry) { this.expiry = expiry; }

    public boolean isUsed() { return used; }
    public void setUsed(boolean used) { this.used = used; }
}
