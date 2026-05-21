package com.carbon.platform.controller;

import com.carbon.platform.dto.response.ApiResponse;
import com.carbon.platform.entity.Certificate;
import com.carbon.platform.service.CertificateService;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/public/certificates")
public class CertificateController {

    private final CertificateService certificateService;

    public CertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @PostMapping("/{certificateId}/generate")
    public ResponseEntity<?> generatePdfIfMissing(@PathVariable String certificateId) {
        try {
            Certificate cert = certificateService.generatePdfIfMissing(certificateId);
            return ResponseEntity.ok().body("PDF generated and saved.");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error generating PDF: " + e.getMessage());
        }
    }
        Certificate cert = certificateService.getCertificateByCode(certificateId);
        byte[] pdf = cert.getPdfData();
        if (pdf == null || pdf.length == 0) {
            return ResponseEntity.notFound().build();
        }
        ByteArrayResource resource = new ByteArrayResource(pdf);
        HttpHeaders headers = new HttpHeaders();
        // Serve the PDF inline so the browser displays it directly
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=certificate-" + certificateId + ".pdf");
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(pdf.length)
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
}
