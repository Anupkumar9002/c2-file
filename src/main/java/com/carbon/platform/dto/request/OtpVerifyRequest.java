package com.carbon.platform.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class OtpVerifyRequest {
    @NotBlank(message = "Target (email or mobile) is required")
    @Schema(example = "9876543210")
    private String target;

    @NotBlank(message = "OTP code is required")
    @Pattern(regexp = "^\\d{6}$", message = "OTP must be exactly 6 digits")
    @Schema(example = "123456")
    private String code;

    // Constructors
    public OtpVerifyRequest() {}

    public OtpVerifyRequest(String target, String code) {
        this.target = target;
        this.code = code;
    }

    // Getters and Setters
    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }
}
