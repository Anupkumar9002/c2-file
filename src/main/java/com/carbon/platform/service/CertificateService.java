package com.carbon.platform.service;

import com.carbon.platform.entity.Certificate;
import com.carbon.platform.entity.Transaction;
import com.carbon.platform.event.CertificateIssuedEvent;
import com.carbon.platform.exception.EntityNotFoundException;
import com.carbon.platform.repository.CertificateRepository;
import com.carbon.platform.util.PdfGenerationUtil;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;
import java.util.UUID;

@Service
public class CertificateService {

    private final CertificateRepository certificateRepository;
    private final ApplicationEventPublisher eventPublisher;

    public CertificateService(CertificateRepository certificateRepository, ApplicationEventPublisher eventPublisher) {
        this.certificateRepository = certificateRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Certificate issueCertificate(Transaction transaction) {
        String certificateId = "CERT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Double offsetValue = transaction.getListing().getQuantity(); // 1 credit = 1 tonne CO2
        String companyName = transaction.getCompany().getCompanyName();

        String secureHash = generateSecureHash(certificateId, transaction.getTransactionId(), companyName, offsetValue);

        String qrCodeUrl = "http://localhost:8080/api/v1/public/certificates/" + certificateId;

        byte[] pdfBytes = null;
        try {
            pdfBytes = PdfGenerationUtil.generateCertificatePdf(
                    companyName,
                    certificateId,
                    offsetValue,
                    secureHash,
                    qrCodeUrl
            );
        } catch (Exception e) {
            System.err.println("Failed to generate PDF for certificate: " + e.getMessage());
        }

        Certificate certificate = new Certificate();
        certificate.setCertificateId(certificateId);
        certificate.setTransaction(transaction);
        certificate.setCo2OffsetValue(offsetValue);
        certificate.setSecureHash(secureHash);
        certificate.setPdfData(pdfBytes);

        Certificate saved = certificateRepository.save(certificate);

        // Publish event to trigger notification
        eventPublisher.publishEvent(new CertificateIssuedEvent(transaction.getCompany().getUser().getId(), certificateId, offsetValue));

        return saved;
    }

    public Certificate getCertificateByCode(String code) {
        return certificateRepository.findByCertificateId(code)
                .orElseThrow(() -> new EntityNotFoundException("Certificate not found with ID: " + code));
    }

    public List<Certificate> getCertificatesByCompany(String companyEmail) {
        return certificateRepository.findByTransactionCompanyUserEmail(companyEmail);
    }

    private String generateSecureHash(String certId, String txId, String companyName, Double offsetValue) {
        try {
            String data = certId + "|" + txId + "|" + companyName + "|" + offsetValue;
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            return "HASH_ERROR_" + System.currentTimeMillis();
        }
    }
}
