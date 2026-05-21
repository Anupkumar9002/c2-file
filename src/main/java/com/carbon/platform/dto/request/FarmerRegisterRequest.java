package com.carbon.platform.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class FarmerRegisterRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Schema(example = "joe@farmer.com")
    private String email;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Mobile must be a valid 10-digit Indian number")
    @Schema(example = "9876543210")
    private String mobile;

    @NotBlank(message = "Password is required")
    @Schema(example = "securePass123")
    private String password;

    @NotBlank(message = "Name is required")
    @Schema(example = "Farmer Joe")
    private String name;

    @NotBlank(message = "Address is required")
    @Schema(example = "Village Green, Farms")
    private String address;

    @NotBlank(message = "Aadhaar number is required")
    @Pattern(regexp = "^\\d{12}$", message = "Aadhaar must be exactly 12 digits")
    @Schema(example = "123456789012")
    private String aadhaarNumber;

    // Constructors
    public FarmerRegisterRequest() {}

    // Getters and Setters
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMobile() { return mobile; }
    public void setMobile(String mobile) { this.mobile = mobile; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    public String getAadhaarNumber() { return aadhaarNumber; }
    public void setAadhaarNumber(String aadhaarNumber) { this.aadhaarNumber = aadhaarNumber; }
}
