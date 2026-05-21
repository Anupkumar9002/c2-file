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

    @GetMapping("/{certificateId}/pdf")
    public ResponseEntity<?> downloadCertificatePdf(@PathVariable String certificateId) {
        Certificate cert = certificateService.getCertificateByCode(certificateId);
        byte[] pdf = cert.getPdfData();
        if (pdf == null || pdf.length == 0) {
            return ResponseEntity.notFound().build();
        }
        ByteArrayResource resource = new ByteArrayResource(pdf);
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=certificate-" + certificateId + ".pdf");
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(pdf.length)
                .contentType(MediaType.APPLICATION_PDF)
                .body(resource);
    }
}
