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

    
}
