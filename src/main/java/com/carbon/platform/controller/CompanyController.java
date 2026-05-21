package com.carbon.platform.controller;

import com.carbon.platform.dto.response.ApiResponse;
import com.carbon.platform.dto.response.CompanyDashboardResponse;
import com.carbon.platform.entity.Certificate;
import com.carbon.platform.entity.Listing;
import com.carbon.platform.entity.Transaction;
import com.carbon.platform.enums.PracticeType;
import com.carbon.platform.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/api/v1/company")
@PreAuthorize("hasRole('COMPANY')")
public class CompanyController {

    private final MarketplaceService marketplaceService;
    private final TransactionService transactionService;
    private final CertificateService certificateService;
    private final DashboardService dashboardService;

    public CompanyController(MarketplaceService marketplaceService,
                             TransactionService transactionService,
                             CertificateService certificateService,
                             DashboardService dashboardService) {
        this.marketplaceService = marketplaceService;
        this.transactionService = transactionService;
        this.certificateService = certificateService;
        this.dashboardService = dashboardService;
    }

    @GetMapping("/listings")
    public ResponseEntity<ApiResponse<List<Listing>>> searchListings(
            @RequestParam(required = false) PracticeType practiceType,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) String location) {

        List<Listing> list = marketplaceService.searchListings(practiceType, minPrice, maxPrice, location);
        return ResponseEntity.ok(new ApiResponse<>(true, "Marketplace listings retrieved.", list));
    }

    @GetMapping("/listings/{id}")
    public ResponseEntity<ApiResponse<Listing>> getListing(@PathVariable Long id) {
        Listing listing = marketplaceService.getListingDetail(id);
        return ResponseEntity.ok(new ApiResponse<>(true, "Retrieved listing details.", listing));
    }

    @PostMapping("/cart")
    public ResponseEntity<ApiResponse<String>> addToCart(Principal principal, @RequestParam Long listingId) {
        marketplaceService.addToCart(principal.getName(), listingId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Listing added to cart successfully."));
    }

    @GetMapping("/cart")
    public ResponseEntity<ApiResponse<List<Listing>>> getCart(Principal principal) {
        List<Listing> list = marketplaceService.getCart(principal.getName());
        return ResponseEntity.ok(new ApiResponse<>(true, "Retrieved cart items.", list));
    }

    @DeleteMapping("/cart/{listingId}")
    public ResponseEntity<ApiResponse<String>> removeFromCart(Principal principal, @PathVariable Long listingId) {
        marketplaceService.removeFromCart(principal.getName(), listingId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Listing removed from cart."));
    }

    @PostMapping("/purchase")
    public ResponseEntity<ApiResponse<Transaction>> purchaseListing(Principal principal, @RequestParam Long listingId) {
        Transaction txn = transactionService.purchaseCredits(principal.getName(), listingId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Carbon credits purchased successfully. Offset certificate issued.", txn));
    }

    @GetMapping("/purchases")
    public ResponseEntity<ApiResponse<List<Transaction>>> getPurchaseHistory(Principal principal) {
        List<Transaction> list = transactionService.getCompanyPurchaseHistory(principal.getName());
        return ResponseEntity.ok(new ApiResponse<>(true, "Retrieved purchase transaction history.", list));
    }

    @PostMapping("/favourites/{listingId}")
    public ResponseEntity<ApiResponse<String>> toggleFavourite(Principal principal, @PathVariable Long listingId) {
        marketplaceService.toggleFavourite(principal.getName(), listingId);
        return ResponseEntity.ok(new ApiResponse<>(true, "Toggled listing in favourites list."));
    }

    @GetMapping("/favourites")
    public ResponseEntity<ApiResponse<List<Listing>>> getFavourites(Principal principal) {
        List<Listing> list = marketplaceService.getFavourites(principal.getName());
        return ResponseEntity.ok(new ApiResponse<>(true, "Retrieved favourite listings.", list));
    }

    @GetMapping("/certificates")
    public ResponseEntity<ApiResponse<List<Certificate>>> getCertificates(Principal principal) {
        List<Certificate> list = certificateService.getCertificatesByCompany(principal.getName());
        return ResponseEntity.ok(new ApiResponse<>(true, "Retrieved offset certificates.", list));
    }

    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<CompanyDashboardResponse>> getDashboard(Principal principal) {
        CompanyDashboardResponse stats = dashboardService.getCompanyDashboard(principal.getName());
        return ResponseEntity.ok(new ApiResponse<>(true, "Retrieved company dashboard KPIs.", stats));
    }
}
