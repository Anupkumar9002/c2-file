package com.carbon.platform.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public class ForgotPasswordRequest {
    @NotBlank(message = "Target (email or mobile) is required")
    @Schema(example = "joe@farmer.com")
    private String target;

    // Constructors
    public ForgotPasswordRequest() {}

    public ForgotPasswordRequest(String target) {
        this.target = target;
    }

    // Getters and Setters
    public String getTarget() { return target; }
    public void setTarget(String target) { this.target = target; }
}
