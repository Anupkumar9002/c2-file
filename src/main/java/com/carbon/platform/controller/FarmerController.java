package com.carbon.platform.controller;

import com.carbon.platform.dto.response.FarmerDashboardResponse;
import com.carbon.platform.dto.response.ApiResponse;
import com.carbon.platform.dto.CarbonAssetDto;
import com.carbon.platform.service.CarbonAssetService;
import com.carbon.platform.entity.*;
import com.carbon.platform.enums.CropCategory;
import com.carbon.platform.enums.GrowingSeason;
import com.carbon.platform.enums.LandType;
import com.carbon.platform.enums.PracticeType;
import com.carbon.platform.service.*;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/farmer")
@PreAuthorize("hasRole('FARMER')")
public class FarmerController {

    private final LandParcelService landParcelService;
    private final PracticeLogService practiceLogService;
    private final WalletService walletService;
    private final DashboardService dashboardService;
    private final CarbonAssetService carbonAssetService;

    public FarmerController(LandParcelService landParcelService,
                            PracticeLogService practiceLogService,
                            WalletService walletService,
                            DashboardService dashboardService,
                            CarbonAssetService carbonAssetService) {
        this.landParcelService = landParcelService;
        this.practiceLogService = practiceLogService;
        this.walletService = walletService;
        this.dashboardService = dashboardService;
        this.carbonAssetService = carbonAssetService;
    }

    @PostMapping("/parcels")
    public ResponseEntity<ApiResponse<LandParcel>> registerParcel(
            Principal principal,
            @RequestParam String parcelName,
            @RequestParam Double areaInAcres,
            @RequestParam String location,
            @RequestParam LandType landType) {

        LandParcel parcel = landParcelService.addParcel(principal.getName(), parcelName, areaInAcres, location, landType);
        return ResponseEntity.ok(new ApiResponse<>(true, "Land parcel registered successfully.", parcel));
    }

    @PutMapping("/parcels/{id}")
    public ResponseEntity<ApiResponse<LandParcel>> editParcel(
            Principal principal,
            @PathVariable Long id,
            @RequestParam(required = false) String parcelName,
            @RequestParam(required = false) Double areaInAcres,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) LandType landType) {

        LandParcel parcel = landParcelService.editParcel(principal.getName(), id, parcelName, areaInAcres, location, landType);
        return ResponseEntity.ok(new ApiResponse<>(true, "Land parcel updated successfully.", parcel));
    }

    @GetMapping("/parcels")
    public ResponseEntity<ApiResponse<List<LandParcel>>> getMyParcels(Principal principal) {
        List<LandParcel> list = landParcelService.getParcelsByFarmer(principal.getName());
        return ResponseEntity.ok(new ApiResponse<>(true, "Retrieved registered land parcels.", list));
    }

    @PostMapping("/practice-logs")
    public ResponseEntity<ApiResponse<PracticeLog>> logPractice(
            Principal principal,
            @RequestParam Long parcelId,
            @RequestParam PracticeType practiceType,
            @RequestParam CropCategory cropCategory,
            @RequestParam GrowingSeason growingSeason,
            @RequestParam Double quantity,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate sowingDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate harvestingDate,
            @RequestBody(required = false) String proofDocumentBase64) {

        PracticeLog log = practiceLogService.logPractice(
                principal.getName(), parcelId, practiceType, cropCategory, growingSeason,
                quantity, sowingDate, harvestingDate, proofDocumentBase64
        );
        return ResponseEntity.ok(new ApiResponse<>(true, "Practice log submitted successfully and is pending admin approval.", log));
    }

    @PutMapping("/practice-logs/{id}")
    public ResponseEntity<ApiResponse<PracticeLog>> resubmitPractice(
            Principal principal,
            @PathVariable Long id,
            @RequestParam(required = false) PracticeType practiceType,
            @RequestParam(required = false) CropCategory cropCategory,
            @RequestParam(required = false) GrowingSeason growingSeason,
            @RequestParam(required = false) Double quantity,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate sowingDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate harvestingDate,
            @RequestBody(required = false) String proofDocumentBase64) {

        PracticeLog log = practiceLogService.resubmitPractice(
                principal.getName(), id, practiceType, cropCategory, growingSeason,
                quantity, sowingDate, harvestingDate, proofDocumentBase64
        );
        return ResponseEntity.ok(new ApiResponse<>(true, "Practice log resubmitted successfully and is pending admin approval.", log));
    }

    @GetMapping("/practice-logs")
    public ResponseEntity<ApiResponse<List<PracticeLog>>> getMyPracticeLogs(Principal principal) {
        List<PracticeLog> list = practiceLogService.getLogsByFarmer(principal.getName());
        return ResponseEntity.ok(new ApiResponse<>(true, "Retrieved practice logs.", list));
    }

    @GetMapping("/wallet")
    public ResponseEntity<ApiResponse<FarmerWallet>> getWallet(Principal principal) {
        FarmerWallet wallet = walletService.getWalletByFarmer(principal.getName());
        return ResponseEntity.ok(new ApiResponse<>(true, "Retrieved wallet balance.", wallet));
    }

    @PostMapping("/withdraw")
    public ResponseEntity<ApiResponse<Withdrawal>> requestWithdrawal(
            Principal principal,
            @RequestParam Double amount,
            @RequestParam(required = false) String bankDetails) {

        Withdrawal withdrawal = walletService.requestWithdrawal(principal.getName(), amount, bankDetails);
        return ResponseEntity.ok(new ApiResponse<>(true, "Withdrawal payout request submitted.", withdrawal));
    }

    @GetMapping("/withdrawals")
    public ResponseEntity<ApiResponse<List<Withdrawal>>> getWithdrawalHistory(Principal principal) {
        List<Withdrawal> list = walletService.getFarmerWithdrawals(principal.getName());
        return ResponseEntity.ok(new ApiResponse<>(true, "Retrieved withdrawal history.", list));
    }

    @GetMapping("/carbon-assets")
    public ResponseEntity<ApiResponse<List<CarbonAssetDto>>> getCarbonAssets(Principal principal) {
        List<CarbonAssetDto> assets = carbonAssetService.getCarbonAssets(principal.getName());
        return ResponseEntity.ok(new ApiResponse<>(true, "Retrieved carbon assets.", assets));
    }
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<FarmerDashboardResponse>> getDashboard(Principal principal) {
        FarmerDashboardResponse resp = dashboardService.getFarmerDashboard(principal.getName());
        return ResponseEntity.ok(new ApiResponse<>(true, "Retrieved farmer dashboard.", resp));
    }

    }
