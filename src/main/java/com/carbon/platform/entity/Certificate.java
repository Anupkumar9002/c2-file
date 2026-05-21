package com.carbon.platform.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "certificates")
public class Certificate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String certificateId;  // UUID
    private String secureHash;     // SHA-256

    @OneToOne
    @JoinColumn(name = "transaction_id")
    private Transaction transaction;

    private Double co2OffsetValue;
    private LocalDateTime issuedAt;
    private LocalDateTime retiredAt;
    private boolean valid = true;

    @Lob
    @Column(columnDefinition = "BLOB")
    private byte[] pdfData;

    // Constructors
    public Certificate() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCertificateId() { return certificateId; }
    public void setCertificateId(String certificateId) { this.certificateId = certificateId; }

    public String getSecureHash() { return secureHash; }
    public void setSecureHash(String secureHash) { this.secureHash = secureHash; }

    public Transaction getTransaction() { return transaction; }
    public void setTransaction(Transaction transaction) { this.transaction = transaction; }

    public Double getCo2OffsetValue() { return co2OffsetValue; }
    public void setCo2OffsetValue(Double co2OffsetValue) { this.co2OffsetValue = co2OffsetValue; }

    public LocalDateTime getIssuedAt() { return issuedAt; }
    public void setIssuedAt(LocalDateTime issuedAt) { this.issuedAt = issuedAt; }

    public LocalDateTime getRetiredAt() { return retiredAt; }
    public void setRetiredAt(LocalDateTime retiredAt) { this.retiredAt = retiredAt; }

    public boolean isValid() { return valid; }
    public void setValid(boolean valid) { this.valid = valid; }

    public byte[] getPdfData() { return pdfData; }
    public void setPdfData(byte[] pdfData) { this.pdfData = pdfData; }
}
