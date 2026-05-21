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
import java.nio.file.*;
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
        java.time.LocalDateTime issuedAt = java.time.LocalDateTime.now();
        try {
            pdfBytes = PdfGenerationUtil.generateCertificatePdf(
                    companyName,
                    certificateId,
                    offsetValue,
                    secureHash,
                    qrCodeUrl,
                    issuedAt,
                    transaction.getCompany().getCompanyName()
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
        certificate.setIssuedAt(issuedAt);

        // Save PDF to file system for later retrieval
        savePdfToFile(certificateId, pdfBytes);

        Certificate saved = certificateRepository.save(certificate);

        // Publish event to trigger notification
        eventPublisher.publishEvent(new CertificateIssuedEvent(transaction.getCompany().getUser().getId(), certificateId, offsetValue));

        return saved;
    }

    public Certificate generatePdfIfMissing(String certificateId) {
        Certificate cert = getCertificateByCode(certificateId);
        if (cert.getPdfData() != null && cert.getPdfData().length > 0) {
            return cert; // PDF already exists
        }
        // Gather data needed for PDF generation
        String companyName = cert.getTransaction().getCompany().getCompanyName();
        Double offsetValue = cert.getCo2OffsetValue();
        String secureHash = cert.getSecureHash();
        String qrCodeUrl = "http://localhost:8080/api/v1/public/certificates/" + certificateId + "/pdf";
        java.time.LocalDateTime issuedAt = java.time.LocalDateTime.now();
        // Placeholder / default values for new fields
        String recipientName = companyName; // using company name as recipient
        String offsetMethod = "Renewable Energy";
        String projectLocation = "Pune, India";
        String verificationStandard = "Gold Standard";
        String transactionHash = cert.getTransaction().getTransactionId();
        String issuerName = "Carbon Platform";
        byte[] pdfBytes = null;
        try {
            pdfBytes = PdfGenerationUtil.generateCertificatePdf(
                    companyName,
                    certificateId,
                    offsetValue,
                    secureHash,
                    qrCodeUrl,
                    issuedAt,
                    recipientName,
                    offsetMethod,
                    projectLocation,
                    verificationStandard,
                    transactionHash,
                    issuerName);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF: " + e.getMessage(), e);
        }
        // Save PDF data to entity and file system
        cert.setPdfData(pdfBytes);
        cert.setIssuedAt(issuedAt);
        certificateRepository.save(cert);
        savePdfToFile(certificateId, pdfBytes);
        return cert;
    }

    public Certificate getCertificateByCode(String code) {
        return certificateRepository.findByCertificateId(code)
                .orElseThrow(() -> new EntityNotFoundException("Certificate not found with ID: " + code));
    }

    public List<Certificate> getCertificatesByCompany(String companyEmail) {
        return certificateRepository.findByTransactionCompanyUserEmail(companyEmail);
    }

    // Helper method to save PDF bytes to a file under "certificates" directory
    private void savePdfToFile(String certificateId, byte[] pdfBytes) {
        try {
            // Determine base directory (project root)
            java.nio.file.Path baseDir = java.nio.file.Paths.get(System.getProperty("user.dir"), "certificates");
            java.nio.file.Files.createDirectories(baseDir);
            java.nio.file.Path filePath = baseDir.resolve("certificate-" + certificateId + ".pdf");
            java.nio.file.Files.write(filePath, pdfBytes);
        } catch (Exception e) {
            System.err.println("Failed to save certificate PDF to file system: " + e.getMessage());
        }
    }

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
