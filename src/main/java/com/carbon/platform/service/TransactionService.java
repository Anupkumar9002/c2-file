package com.carbon.platform.service;

import com.carbon.platform.entity.*;
import com.carbon.platform.enums.CreditStatus;
import com.carbon.platform.enums.ListingStatus;
import com.carbon.platform.enums.PaymentStatus;
import com.carbon.platform.event.CreditSaleEvent;
import com.carbon.platform.exception.EntityNotFoundException;
import com.carbon.platform.exception.InvalidActionException;
import com.carbon.platform.repository.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final ListingRepository listingRepository;
    private final CompanyRepository companyRepository;
    private final CarbonCreditRepository carbonCreditRepository;
    private final WalletService walletService;
    private final CertificateService certificateService;
    private final ApplicationEventPublisher eventPublisher;

    @Value("${app.platform.fee.percentage:2.5}")
    private double platformFeePercentage;

    public TransactionService(TransactionRepository transactionRepository,
                              ListingRepository listingRepository,
                              CompanyRepository companyRepository,
                              CarbonCreditRepository carbonCreditRepository,
                              WalletService walletService,
                              CertificateService certificateService,
                              ApplicationEventPublisher eventPublisher) {
        this.transactionRepository = transactionRepository;
        this.listingRepository = listingRepository;
        this.companyRepository = companyRepository;
        this.carbonCreditRepository = carbonCreditRepository;
        this.walletService = walletService;
        this.certificateService = certificateService;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public Transaction purchaseCredits(String companyEmail, Long listingId) {
        Company company = companyRepository.findByUserEmail(companyEmail)
                .orElseThrow(() -> new EntityNotFoundException("Company profile not found for user: " + companyEmail));

        if (company.getApprovalStatus() != com.carbon.platform.enums.ApprovalStatus.APPROVED) {
            throw new InvalidActionException("Only APPROVED companies can purchase carbon credits.");
        }

        Listing listing = listingRepository.findById(listingId)
                .orElseThrow(() -> new EntityNotFoundException("Listing not found with ID: " + listingId));

        if (listing.getStatus() != ListingStatus.ACTIVE) {
            throw new InvalidActionException("This carbon credit listing is no longer active.");
        }

        // Calculate pricing and platform fees
        double quantity = listing.getQuantity();
        double price = listing.getPricePerCredit();
        double totalAmount = quantity * price;
        double platformFee = totalAmount * (platformFeePercentage / 100.0);
        double netAmount = totalAmount - platformFee;

        // Perform payment (mock transaction simulation)
        PaymentStatus paymentStatus = simulatePaymentGateway(totalAmount);
        if (paymentStatus != PaymentStatus.SUCCESS) {
            throw new InvalidActionException("Payment transaction failed. Please retry.");
        }

        // Save transaction record
        String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Transaction txn = new Transaction();
        txn.setTransactionId(transactionId);
        txn.setCompany(company);
        txn.setListing(listing);
        txn.setQuantity(quantity);
        txn.setTotalAmount(totalAmount);
        txn.setPlatformFee(platformFee);
        txn.setNetAmount(netAmount);
        txn.setPaymentStatus(paymentStatus);
        
        Transaction savedTxn = transactionRepository.save(txn);

        // Update Listing and CarbonCredit states
        listing.setStatus(ListingStatus.SOLD);
        listingRepository.save(listing);

        CarbonCredit credit = listing.getCarbonCredit();
        credit.setStatus(CreditStatus.RETIRED); // Credits retired after purchase
        carbonCreditRepository.save(credit);

        // Atomically credit farmer's wallet (net amount)
        walletService.creditWallet(listing.getFarmer().getId(), netAmount);

        // Issue offset certificate
        certificateService.issueCertificate(savedTxn);

        // Publish event to trigger notification handlers
        eventPublisher.publishEvent(new CreditSaleEvent(
                listing.getFarmer().getUser().getId(),
                company.getUser().getId(),
                quantity,
                totalAmount,
                netAmount
        ));

        return savedTxn;
    }

    public List<Transaction> getCompanyPurchaseHistory(String companyEmail) {
        return transactionRepository.findByCompanyUserEmail(companyEmail);
    }

    public List<Transaction> getFarmerSalesHistory(String farmerEmail) {
        return transactionRepository.findByListingFarmerUserEmail(farmerEmail);
    }

    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    private PaymentStatus simulatePaymentGateway(double amount) {
        // Integration simulation: payment is always approved for demo/H2
        System.out.println("[PAYMENT GATEWAY] Processed payment of INR " + amount + " successfully.");
        return PaymentStatus.SUCCESS;
    }
}
