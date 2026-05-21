package com.carbon.platform.controller;

import com.carbon.platform.dto.response.ApiResponse;
import com.carbon.platform.entity.Certificate;
import com.carbon.platform.service.CertificateService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/public/certificates")
public class PublicCertificateController {

    private final CertificateService certificateService;

    public PublicCertificateController(CertificateService certificateService) {
        this.certificateService = certificateService;
    }

    @GetMapping("/{certificateId}")
    public ResponseEntity<ApiResponse<Certificate>> verifyCertificate(@PathVariable String certificateId) {
        Certificate certificate = certificateService.getCertificateByCode(certificateId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Certificate verification details retrieved.", certificate));
    }

    @GetMapping("/{certificateId}/pdf")
    public ResponseEntity<byte[]> downloadCertificatePdf(@PathVariable String certificateId) {
        Certificate certificate = certificateService.getCertificateByCode(certificateId);
        byte[] pdfBytes = certificate.getPdfData();

        if (pdfBytes == null || pdfBytes.length == 0) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("inline", "certificate_" + certificateId + ".pdf");
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
