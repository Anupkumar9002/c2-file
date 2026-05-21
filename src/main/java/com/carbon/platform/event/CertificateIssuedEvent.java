package com.carbon.platform.event;

public class CertificateIssuedEvent {
    private final Long userId;
    private final String certificateId;
    private final Double offsetValue;

    public CertificateIssuedEvent(Long userId, String certificateId, Double offsetValue) {
        this.userId = userId;
        this.certificateId = certificateId;
        this.offsetValue = offsetValue;
    }

    public Long getUserId() { return userId; }
    public String getCertificateId() { return certificateId; }
    public Double getOffsetValue() { return offsetValue; }
}
