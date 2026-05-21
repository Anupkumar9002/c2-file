package com.carbon.platform.service;

import com.carbon.platform.dto.response.*;
import com.carbon.platform.entity.*;
import com.carbon.platform.enums.*;
import com.carbon.platform.repository.*;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DashboardService {

    private final FarmerRepository farmerRepository;
    private final CompanyRepository companyRepository;
    private final LandParcelRepository landParcelRepository;
    private final PracticeLogRepository practiceLogRepository;
    private final CarbonCreditRepository carbonCreditRepository;
    private final ListingRepository listingRepository;
    private final TransactionRepository transactionRepository;
    private final CertificateRepository certificateRepository;
    private final FarmerWalletRepository walletRepository;
    private final MarketplaceService marketplaceService;

    public DashboardService(FarmerRepository farmerRepository,
                            CompanyRepository companyRepository,
                            LandParcelRepository landParcelRepository,
                            PracticeLogRepository practiceLogRepository,
                            CarbonCreditRepository carbonCreditRepository,
                            ListingRepository listingRepository,
                            TransactionRepository transactionRepository,
                            CertificateRepository certificateRepository,
                            FarmerWalletRepository walletRepository,
                            MarketplaceService marketplaceService) {
        this.farmerRepository = farmerRepository;
        this.companyRepository = companyRepository;
        this.landParcelRepository = landParcelRepository;
        this.practiceLogRepository = practiceLogRepository;
        this.carbonCreditRepository = carbonCreditRepository;
        this.listingRepository = listingRepository;
        this.transactionRepository = transactionRepository;
        this.certificateRepository = certificateRepository;
        this.walletRepository = walletRepository;
        this.marketplaceService = marketplaceService;
    }

    public FarmerDashboardResponse getFarmerDashboard(String farmerEmail) {
        Farmer farmer = farmerRepository.findByUserEmail(farmerEmail).orElse(null);
        if (farmer == null) {
            return new FarmerDashboardResponse(0.0, 0.0, 0.0, 0.0, 0, 0);
        }

        FarmerWallet wallet = walletRepository.findByFarmerId(farmer.getId()).orElse(new FarmerWallet());
        
        List<LandParcel> parcels = landParcelRepository.findByFarmerId(farmer.getId());
        double totalArea = parcels.stream().mapToDouble(p -> p.getAreaInAcres() != null ? p.getAreaInAcres() : 0.0).sum();

        List<CarbonCredit> credits = carbonCreditRepository.findByFarmerId(farmer.getId());
        double totalCreditsEarned = credits.stream()
                .filter(c -> c.getStatus() == CreditStatus.VERIFIED || c.getStatus() == CreditStatus.LISTED || c.getStatus() == CreditStatus.RETIRED)
                .mapToDouble(c -> c.getFinalCredits() != null ? c.getFinalCredits() : 0.0).sum();

        List<Listing> listings = listingRepository.findByFarmerId(farmer.getId());
        double totalCreditsSold = listings.stream()
                .filter(l -> l.getStatus() == ListingStatus.SOLD)
                .mapToDouble(l -> l.getQuantity() != null ? l.getQuantity() : 0.0).sum();

        List<PracticeLog> logs = practiceLogRepository.findByFarmerId(farmer.getId());

        return new FarmerDashboardResponse(
                wallet.getBalance(),
                totalArea,
                totalCreditsEarned,
                totalCreditsSold,
                parcels.size(),
                logs.size()
        );
    }

    public CompanyDashboardResponse getCompanyDashboard(String companyEmail) {
        Company company = companyRepository.findByUserEmail(companyEmail).orElse(null);
        if (company == null) {
            return new CompanyDashboardResponse(0, 0.0, 0.0, 0);
        }

        List<Certificate> certs = certificateRepository.findByTransactionCompanyUserEmail(companyEmail);
        double totalOffset = certs.stream()
                .mapToDouble(c -> c.getCo2OffsetValue() != null ? c.getCo2OffsetValue() : 0.0).sum();

        List<Transaction> txns = transactionRepository.findByCompanyUserEmail(companyEmail);
        double totalSpend = txns.stream()
                .mapToDouble(t -> t.getTotalAmount() != null ? t.getTotalAmount() : 0.0).sum();

        int cartCount = marketplaceService.getCart(companyEmail).size();

        return new CompanyDashboardResponse(
                certs.size(),
                totalOffset,
                totalSpend,
                cartCount
        );
    }

    public AdminDashboardResponse getAdminDashboard() {
        long totalFarmers = farmerRepository.count();
        long totalCompanies = companyRepository.count();

        long pendingFarmers = farmerRepository.findAll().stream()
                .filter(f -> f.getApprovalStatus() == ApprovalStatus.PENDING).count();

        long pendingCompanies = companyRepository.findAll().stream()
                .filter(c -> c.getApprovalStatus() == ApprovalStatus.PENDING).count();

        long pendingLogs = practiceLogRepository.findAll().stream()
                .filter(l -> l.getStatus() == PracticeStatus.PENDING).count();

        List<Transaction> txns = transactionRepository.findAll();
        double totalEarnings = txns.stream()
                .mapToDouble(t -> t.getPlatformFee() != null ? t.getPlatformFee() : 0.0).sum();

        return new AdminDashboardResponse(
                totalFarmers,
                totalCompanies,
                pendingFarmers,
                pendingCompanies,
                pendingLogs,
                txns.size(),
                totalEarnings
        );
    }
}
