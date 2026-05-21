package com.carbon.platform.controller;

import com.carbon.platform.dto.response.AdminDashboardResponse;
import com.carbon.platform.dto.response.ApiResponse;
import com.carbon.platform.entity.*;
import com.carbon.platform.enums.*;
import com.carbon.platform.service.*;
import com.carbon.platform.repository.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/admin")
@PreAuthorize("hasAnyRole('ADMIN', 'SUPERVISOR')")
public class AdminController {

    private final AdminService adminService;
    private final WalletService walletService;
    private final DashboardService dashboardService;
    private final AuditService auditService;
    private final FarmerRepository farmerRepository;
    private final CompanyRepository companyRepository;
    private final PracticeLogRepository practiceLogRepository;
    private final CarbonCreditRepository carbonCreditRepository;

    public AdminController(AdminService adminService,
                           WalletService walletService,
                           DashboardService dashboardService,
                           AuditService auditService,
                           FarmerRepository farmerRepository,
                           CompanyRepository companyRepository,
                           PracticeLogRepository practiceLogRepository,
                           CarbonCreditRepository carbonCreditRepository) {
        this.adminService = adminService;
        this.walletService = walletService;
        this.dashboardService = dashboardService;
        this.auditService = auditService;
        this.farmerRepository = farmerRepository;
        this.companyRepository = companyRepository;
        this.practiceLogRepository = practiceLogRepository;
        this.carbonCreditRepository = carbonCreditRepository;
    }

    @PostMapping("/verify/farmer")
    public ResponseEntity<ApiResponse<String>> verifyFarmer(
            @RequestParam Long farmerId,
            @RequestParam ApprovalStatus status,
            @RequestParam String comments) {

        adminService.verifyFarmer(farmerId, status, comments);
        return ResponseEntity.ok(new ApiResponse<>(true, "Farmer profile verification completed. Status: " + status));
    }

    @PostMapping("/verify/company")
    public ResponseEntity<ApiResponse<String>> verifyCompany(
            @RequestParam Long companyId,
            @RequestParam ApprovalStatus status,
            @RequestParam String comments) {

        adminService.verifyCompany(companyId, status, comments);
        return ResponseEntity.ok(new ApiResponse<>(true, "Company profile verification completed. Status: " + status));
    }

    @PostMapping("/verify/practice-log")
    public ResponseEntity<ApiResponse<String>> verifyPracticeLog(
            @RequestParam Long logId,
            @RequestParam PracticeStatus status,
            @RequestParam String comments) {

        adminService.verifyPracticeLog(logId, status, comments);
        return ResponseEntity.ok(new ApiResponse<>(true, "Practice log verification completed. Status: " + status));
    }

    @PostMapping("/verify/carbon-credit")
    public ResponseEntity<ApiResponse<String>> verifyCarbonCredit(
            @RequestParam Long creditId,
            @RequestParam CreditStatus status,
            @RequestParam String comments,
            @RequestParam(required = false) Double pricePerCredit,
            @RequestParam(required = false) Double quantity) {

        adminService.verifyCarbonCredit(creditId, status, comments, pricePerCredit, quantity);
        return ResponseEntity.ok(new ApiResponse<>(true, "Carbon credit verification completed. Status: " + status));
    }

    @PostMapping("/marketplace/list")
    public ResponseEntity<ApiResponse<String>> listVerifiedCredit(
            @RequestParam Long creditId,
            @RequestParam Double pricePerCredit,
            @RequestParam Double quantity,
            @RequestParam(required = false) @org.springframework.format.annotation.DateTimeFormat(iso = org.springframework.format.annotation.DateTimeFormat.ISO.DATE) java.time.LocalDate validUntil) {

        adminService.listVerifiedCredit(creditId, pricePerCredit, quantity, validUntil);
        return ResponseEntity.ok(new ApiResponse<>(true, "Carbon credit successfully listed on marketplace."));
    }

    @GetMapping("/farmers")
    public ResponseEntity<ApiResponse<List<Farmer>>> getFarmers() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Retrieved farmers.", farmerRepository.findAll()));
    }

    @GetMapping("/companies")
    public ResponseEntity<ApiResponse<List<Company>>> getCompanies() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Retrieved companies.", companyRepository.findAll()));
    }

    @GetMapping("/practice-logs")
    public ResponseEntity<ApiResponse<List<PracticeLog>>> getPracticeLogs() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Retrieved practice logs.", practiceLogRepository.findAll()));
    }

    @GetMapping("/carbon-credits")
    public ResponseEntity<ApiResponse<List<CarbonCredit>>> getCarbonCredits() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Retrieved carbon credits.", carbonCreditRepository.findAll()));
    }

    @PostMapping("/verify/withdrawal/approve")
    public ResponseEntity<ApiResponse<String>> approveWithdrawal(
            @RequestParam Long withdrawalId,
            @RequestParam String comments) {

        walletService.approveWithdrawal(withdrawalId, comments);
        return ResponseEntity.ok(new ApiResponse<>(true, "Withdrawal payout request approved."));
    }

    @PostMapping("/verify/withdrawal/reject")
    public ResponseEntity<ApiResponse<String>> rejectWithdrawal(
            @RequestParam Long withdrawalId,
            @RequestParam String comments) {

        walletService.rejectWithdrawal(withdrawalId, comments);
        return ResponseEntity.ok(new ApiResponse<>(true, "Withdrawal payout request rejected and funds refunded to wallet."));
    }

    @GetMapping("/withdrawals")
    public ResponseEntity<ApiResponse<List<Withdrawal>>> getWithdrawals() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Retrieved withdrawal requests.", walletService.getAllWithdrawals()));
    }

    @PostMapping("/formula")
    public ResponseEntity<ApiResponse<String>> configureFormula(
            @RequestParam PracticeType practiceType,
            @RequestParam Double baseCoefficient,
            @RequestParam Double maxCap,
            @RequestParam String version) {

        adminService.configureFormula(practiceType, baseCoefficient, maxCap, version);
        return ResponseEntity.ok(new ApiResponse<>(true, "Credit calculation formula updated successfully."));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<AdminDashboardResponse>> getDashboard() {
        AdminDashboardResponse stats = dashboardService.getAdminDashboard();
        return ResponseEntity.ok(new ApiResponse<>(true, "Retrieved platform administrator dashboard.", stats));
    }

    @GetMapping("/audit-logs")
    public ResponseEntity<ApiResponse<List<AuditLog>>> getAuditLogs() {
        return ResponseEntity.ok(new ApiResponse<>(true, "Retrieved audit log history.", auditService.getAllAuditLogs()));
    }
}
