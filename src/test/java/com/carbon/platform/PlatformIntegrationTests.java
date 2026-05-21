package com.carbon.platform;

import com.carbon.platform.dto.request.*;
import com.carbon.platform.dto.response.*;
import com.carbon.platform.entity.*;
import com.carbon.platform.enums.*;
import com.carbon.platform.repository.*;
import com.carbon.platform.service.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class PlatformIntegrationTests {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private FarmerRepository farmerRepository;

    @Autowired
    private CompanyRepository companyRepository;

    @Autowired
    private LandParcelRepository landParcelRepository;

    @Autowired
    private PracticeLogRepository practiceLogRepository;

    @Autowired
    private CarbonCreditRepository carbonCreditRepository;

    @Autowired
    private ListingRepository listingRepository;

    @Autowired
    private FarmerWalletRepository walletRepository;

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private CertificateRepository certificateRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private AdminService adminService;

    @Autowired
    private LandParcelService landParcelService;

    @Autowired
    private PracticeLogService practiceLogService;

    @Autowired
    private MarketplaceService marketplaceService;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private WalletService walletService;

    @Autowired
    private CertificateService certificateService;

    @Test
    public void testEndToEndFlow() throws Exception {
        // 1. Register Farmer
        FarmerRegisterRequest farmerReq = new FarmerRegisterRequest();
        farmerReq.setName("Farmer Joe");
        farmerReq.setEmail("joe@farmer.com");
        farmerReq.setMobile("9876543210");
        farmerReq.setAddress("Village Green, Farms");
        farmerReq.setPassword("securePass123");
        farmerReq.setAadhaarNumber("123456789012");
        
        userService.registerFarmer(farmerReq);
        
        // Assert OTP generated
        String otp = otpService.generateOtp("9876543210");
        Assertions.assertNotNull(otp);
        
        // Verify Registration OTP
        OtpVerifyRequest verifyReq = new OtpVerifyRequest("9876543210", otp);
        boolean otpVerified = userService.verifyRegistrationOtp(verifyReq);
        Assertions.assertTrue(otpVerified);
        
        // 2. Admin Verifies and Approves Farmer
        Farmer farmer = farmerRepository.findByUserEmail("joe@farmer.com").orElse(null);
        Assertions.assertNotNull(farmer);
        Assertions.assertEquals(ApprovalStatus.PENDING, farmer.getApprovalStatus());
        
        adminService.verifyFarmer(farmer.getId(), ApprovalStatus.APPROVED, "Farmer document looks good.");
        
        Farmer approvedFarmer = farmerRepository.findById(farmer.getId()).orElse(null);
        Assertions.assertEquals(ApprovalStatus.APPROVED, approvedFarmer.getApprovalStatus());
        Assertions.assertNotNull(approvedFarmer.getFarmerId());
        
        // 3. Farmer Adds a Land Parcel
        LandParcel parcel = landParcelService.addParcel("joe@farmer.com", "North Field", 10.0, "Zone A", LandType.AGRICULTURAL);
        Assertions.assertNotNull(parcel.getId());
        
        // 4. Farmer Logs Practice Log
        PracticeLog log = practiceLogService.logPractice(
                "joe@farmer.com",
                parcel.getId(),
                PracticeType.TREE_PLANTATION,
                CropCategory.FOOD,
                GrowingSeason.KHARIF,
                100.0,
                LocalDate.now().minusMonths(4),
                LocalDate.now().minusMonths(1),
                "ProofDataString"
        );
        Assertions.assertNotNull(log.getId());
        Assertions.assertEquals(PracticeStatus.PENDING, log.getStatus());
        
        // 5. Admin Approves Practice Log (triggers calculation engine)
        adminService.verifyPracticeLog(log.getId(), PracticeStatus.APPROVED, "Verified sowing dates and yield reports.");
        
        PracticeLog approvedLog = practiceLogRepository.findById(log.getId()).orElse(null);
        Assertions.assertEquals(PracticeStatus.APPROVED, approvedLog.getStatus());
        
        // Assert CarbonCredit created
        List<CarbonCredit> credits = carbonCreditRepository.findByFarmerId(approvedFarmer.getId());
        Assertions.assertFalse(credits.isEmpty());
        CarbonCredit credit = credits.get(0);
        Assertions.assertEquals(CreditStatus.PENDING_VERIFICATION, credit.getStatus());
        Assertions.assertTrue(credit.getFinalCredits() > 0.0);
        
        // 6. Admin Verifies Carbon Credits to make them sellable
        adminService.verifyCarbonCredit(credit.getId(), CreditStatus.VERIFIED, "Calculations verified.");
        CarbonCredit verifiedCredit = carbonCreditRepository.findById(credit.getId()).orElse(null);
        Assertions.assertEquals(CreditStatus.VERIFIED, verifiedCredit.getStatus());
        
        // 7. Farmer Lists Carbon Credits on Marketplace
        Listing listing = marketplaceService.createListing("joe@farmer.com", credit.getId(), 5.0, 15.0, null);
        Assertions.assertNotNull(listing.getId());
        Assertions.assertEquals(ListingStatus.ACTIVE, listing.getStatus());
        
        // Verify remaining credit status is LISTED
        CarbonCredit listedCredit = carbonCreditRepository.findById(credit.getId()).orElse(null);
        Assertions.assertEquals(CreditStatus.LISTED, listedCredit.getStatus());
        
        // 8. Register and Approve Company
        CompanyRegisterRequest compReq = new CompanyRegisterRequest();
        compReq.setName("GreenCorp Inc");
        compReq.setEmail("greencorp@corp.com");
        compReq.setMobile("9777777777");
        compReq.setAddress("Industrial District");
        compReq.setPassword("corpPass123");
        compReq.setCompanyName("GreenCorp LLC");
        compReq.setGstNumber("22AAAAA0000A1Z5");
        compReq.setPanNumber("ABCDE1234F");
        
        userService.registerCompany(compReq);
        Company company = companyRepository.findByUserEmail("greencorp@corp.com").orElse(null);
        Assertions.assertNotNull(company);
        
        String companyOtp = otpService.generateOtp("greencorp@corp.com");
        userService.verifyRegistrationOtp(new OtpVerifyRequest("greencorp@corp.com", companyOtp));
        
        adminService.verifyCompany(company.getId(), ApprovalStatus.APPROVED, "Company registration document verified.");
        
        // 9. Company Purchases Carbon Credits
        Transaction transaction = transactionService.purchaseCredits("greencorp@corp.com", listing.getId());
        Assertions.assertNotNull(transaction.getId());
        Assertions.assertEquals(PaymentStatus.SUCCESS, transaction.getPaymentStatus());
        
        // Verify listing status is now SOLD
        Listing soldListing = listingRepository.findById(listing.getId()).orElse(null);
        Assertions.assertEquals(ListingStatus.SOLD, soldListing.getStatus());
        
        // Verify carbon credit status is now RETIRED
        CarbonCredit retiredCredit = carbonCreditRepository.findById(credit.getId()).orElse(null);
        Assertions.assertEquals(CreditStatus.RETIRED, retiredCredit.getStatus());
        
        // 10. Verify Wallet Credits & Platform Fees (2.5% fee on 75 INR = 1.875, Farmer balance = 73.125)
        FarmerWallet wallet = walletRepository.findByFarmerId(approvedFarmer.getId()).orElse(null);
        Assertions.assertNotNull(wallet);
        Assertions.assertEquals(73.125, wallet.getBalance(), 0.001);
        Assertions.assertEquals(73.125, wallet.getTotalEarned(), 0.001);
        
        // 11. Verify Offset Certificate
        List<Certificate> certs = certificateRepository.findByTransactionCompanyUserEmail("greencorp@corp.com");
        Assertions.assertFalse(certs.isEmpty());
        Certificate certificate = certs.get(0);
        Assertions.assertNotNull(certificate.getCertificateId());
        Assertions.assertNotNull(certificate.getSecureHash());
        Assertions.assertNotNull(certificate.getPdfData());
        Assertions.assertTrue(certificate.getPdfData().length > 0);
        
        // 12. Public Certificate Verification Verification
        Certificate verifiedCert = certificateService.getCertificateByCode(certificate.getCertificateId());
        Assertions.assertEquals(certificate.getSecureHash(), verifiedCert.getSecureHash());
        Assertions.assertEquals(5.0, verifiedCert.getCo2OffsetValue());
    }
}
