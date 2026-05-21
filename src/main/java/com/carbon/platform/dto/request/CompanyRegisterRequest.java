package com.carbon.platform.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CompanyRegisterRequest {
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Schema(example = "greencorp@corp.com")
    private String email;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Mobile must be a valid 10-digit Indian number")
    @Schema(example = "9777777777")
    private String mobile;

    @NotBlank(message = "Password is required")
    @Schema(example = "corpPass123")
    private String password;

    @NotBlank(message = "Contact person name is required")
    @Schema(example = "Sarah Connor")
    private String name;

    @NotBlank(message = "Address is required")
    @Schema(example = "Industrial District, Sector 4")
    private String address;

    @NotBlank(message = "Company name is required")
    @Schema(example = "GreenCorp LLC")
    private String companyName;

    @NotBlank(message = "GST number is required")
    @Size(min = 15, max = 15, message = "GST number must be exactly 15 characters")
    @Schema(example = "22AAAAA0000A1Z5")
    private String gstNumber;

    @NotBlank(message = "PAN number is required")
    @Size(min = 10, max = 10, message = "PAN number must be exactly 10 characters")
    @Schema(example = "ABCDE1234F")
    private String panNumber;

    // Constructors
    public CompanyRegisterRequest() {}

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

    public String getCompanyName() { return companyName; }
    public void setCompanyName(String companyName) { this.companyName = companyName; }

    public String getGstNumber() { return gstNumber; }
    public void setGstNumber(String gstNumber) { this.gstNumber = gstNumber; }

    public String getPanNumber() { return panNumber; }
    public void setPanNumber(String panNumber) { this.panNumber = panNumber; }
}
