package com.carbon.platform.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ResetPasswordRequest {
    @NotBlank(message = "Target (email or mobile) is required")
    @Schema(example = "joe@farmer.com")
    private String target;

    @NotBlank(message = "OTP code is required")
    @Pattern(regexp = "^\\d{6}$", message = "OTP must be exactly 6 digits")
    @Schema(example = "123456")
    private String code;

    @NotBlank(message = "New password is required")
    @Schema(example = "newSecurePass123")
    private String newPassword;

    // Constructors
    public ResetPasswordRequest() {}

    // Getters and Setters
    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getNewPassword() { return newPassword; }
    public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
}
