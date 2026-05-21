package com.carbon.platform.controller;

import com.carbon.platform.dto.request.*;
import com.carbon.platform.dto.response.ApiResponse;
import com.carbon.platform.dto.response.LoginResponse;
import com.carbon.platform.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/register/farmer")
    public ResponseEntity<ApiResponse<String>> registerFarmer(@Valid @RequestBody FarmerRegisterRequest request) {
        String otp = userService.registerFarmer(request);
        ApiResponse<String> response = new ApiResponse<>(true, "Farmer registration initiated. Verification OTP is: " + otp);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register/company")
    public ResponseEntity<ApiResponse<String>> registerCompany(@Valid @RequestBody CompanyRegisterRequest request) {
        String otp = userService.registerCompany(request);
        ApiResponse<String> response = new ApiResponse<>(true, "Company registration initiated. Verification OTP is: " + otp);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<ApiResponse<String>> verifyOtp(@Valid @RequestBody OtpVerifyRequest request) {
        boolean verified = userService.verifyRegistrationOtp(request);
        if (verified) {
            return ResponseEntity.ok(new ApiResponse<>(true, "OTP verified successfully. Your registration is complete and pending admin verification."));
        }
        return ResponseEntity.badRequest().body(new ApiResponse<>(false, "Invalid or expired OTP"));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse data = userService.login(request);
        ApiResponse<LoginResponse> response = new ApiResponse<>(true, "Login successful", data);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<String>> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
        String otp = userService.initiateForgotPassword(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Verification OTP generated. Your OTP is: " + otp));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<String>> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
        userService.resetPassword(request);
        return ResponseEntity.ok(new ApiResponse<>(true, "Password has been reset successfully."));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse<String>> updateProfile(Principal principal, @RequestParam String name, @RequestParam String address) {
        if (principal == null) {
            return ResponseEntity.status(401).body(new ApiResponse<>(false, "Unauthorized"));
        }
        userService.updateProfile(principal.getName(), name, address);
        return ResponseEntity.ok(new ApiResponse<>(true, "Profile updated successfully."));
    }
}
