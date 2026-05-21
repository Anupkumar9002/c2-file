package com.carbon.platform.service;

import com.carbon.platform.entity.Farmer;
import com.carbon.platform.entity.FarmerWallet;
import com.carbon.platform.entity.Withdrawal;
import com.carbon.platform.enums.WithdrawalStatus;
import com.carbon.platform.event.WalletCreditEvent;
import com.carbon.platform.event.WithdrawalStatusEvent;
import com.carbon.platform.exception.EntityNotFoundException;
import com.carbon.platform.exception.InsufficientCreditsException;
import com.carbon.platform.exception.InvalidActionException;
import com.carbon.platform.repository.FarmerRepository;
import com.carbon.platform.repository.FarmerWalletRepository;
import com.carbon.platform.repository.WithdrawalRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class WalletService {

    private final FarmerWalletRepository walletRepository;
    private final WithdrawalRepository withdrawalRepository;
    private final FarmerRepository farmerRepository;
    private final ApplicationEventPublisher eventPublisher;

    public WalletService(FarmerWalletRepository walletRepository,
                         WithdrawalRepository withdrawalRepository,
                         FarmerRepository farmerRepository,
                         ApplicationEventPublisher eventPublisher) {
        this.walletRepository = walletRepository;
        this.withdrawalRepository = withdrawalRepository;
        this.farmerRepository = farmerRepository;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public void creditWallet(Long farmerId, Double amount) {
        FarmerWallet wallet = walletRepository.findByFarmerId(farmerId)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found for farmer ID: " + farmerId));

        wallet.setBalance(wallet.getBalance() + amount);
        wallet.setTotalEarned(wallet.getTotalEarned() + amount);
        walletRepository.save(wallet);

        // Publish credit event
        eventPublisher.publishEvent(new WalletCreditEvent(wallet.getFarmer().getUser().getId(), amount, wallet.getBalance()));
    }

    @Transactional
    public Withdrawal requestWithdrawal(String farmerEmail, Double amount, String bankDetails) {
        Farmer farmer = farmerRepository.findByUserEmail(farmerEmail)
                .orElseThrow(() -> new EntityNotFoundException("Farmer profile not found"));

        FarmerWallet wallet = walletRepository.findByFarmerId(farmer.getId())
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found"));

        if (amount <= 0) {
            throw new InvalidActionException("Withdrawal amount must be a positive decimal.");
        }

        if (wallet.getBalance() < amount) {
            throw new InsufficientCreditsException("Insufficient wallet balance for withdrawal.");
        }

        // Atomically debit wallet
        wallet.setBalance(wallet.getBalance() - amount);
        wallet.setTotalWithdrawn(wallet.getTotalWithdrawn() + amount);
        walletRepository.save(wallet);

        Withdrawal withdrawal = new Withdrawal();
        withdrawal.setFarmer(farmer);
        withdrawal.setAmount(amount);
        withdrawal.setBankDetails(bankDetails != null ? bankDetails : "Default Bank Account");
        withdrawal.setStatus(WithdrawalStatus.PENDING);
        
        Withdrawal saved = withdrawalRepository.save(withdrawal);

        eventPublisher.publishEvent(new WithdrawalStatusEvent(farmer.getUser().getId(), amount, "PENDING", "Withdrawal requested."));
        return saved;
    }

    @Transactional
    public void approveWithdrawal(Long withdrawalId, String comments) {
        if (comments == null || comments.trim().isEmpty()) {
            throw new InvalidActionException("Admin review comments are mandatory.");
        }

        Withdrawal withdrawal = withdrawalRepository.findById(withdrawalId)
                .orElseThrow(() -> new EntityNotFoundException("Withdrawal request not found with ID: " + withdrawalId));

        if (withdrawal.getStatus() != WithdrawalStatus.PENDING) {
            throw new InvalidActionException("Only PENDING withdrawals can be approved.");
        }

        withdrawal.setStatus(WithdrawalStatus.APPROVED);
        withdrawal.setAdminComments(comments);
        withdrawal.setProcessedAt(LocalDateTime.now());
        withdrawalRepository.save(withdrawal);

        eventPublisher.publishEvent(new WithdrawalStatusEvent(
                withdrawal.getFarmer().getUser().getId(),
                withdrawal.getAmount(),
                "APPROVED",
                comments
        ));
    }

    @Transactional
    public void rejectWithdrawal(Long withdrawalId, String comments) {
        if (comments == null || comments.trim().isEmpty()) {
            throw new InvalidActionException("Admin review comments are mandatory.");
        }

        Withdrawal withdrawal = withdrawalRepository.findById(withdrawalId)
                .orElseThrow(() -> new EntityNotFoundException("Withdrawal request not found with ID: " + withdrawalId));

        if (withdrawal.getStatus() != WithdrawalStatus.PENDING) {
            throw new InvalidActionException("Only PENDING withdrawals can be rejected.");
        }

        withdrawal.setStatus(WithdrawalStatus.REJECTED);
        withdrawal.setAdminComments(comments);
        withdrawalRepository.save(withdrawal);

        // Refund wallet
        Farmer farmer = withdrawal.getFarmer();
        FarmerWallet wallet = walletRepository.findByFarmerId(farmer.getId())
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found"));

        wallet.setBalance(wallet.getBalance() + withdrawal.getAmount());
        wallet.setTotalWithdrawn(wallet.getTotalWithdrawn() - withdrawal.getAmount());
        walletRepository.save(wallet);

        eventPublisher.publishEvent(new WithdrawalStatusEvent(
                farmer.getUser().getId(),
                withdrawal.getAmount(),
                "REJECTED",
                comments
        ));
    }

    public FarmerWallet getWalletByFarmer(String farmerEmail) {
        return walletRepository.findByFarmerUserEmail(farmerEmail)
                .orElseThrow(() -> new EntityNotFoundException("Wallet not found for farmer: " + farmerEmail));
    }

    public List<Withdrawal> getFarmerWithdrawals(String farmerEmail) {
        return withdrawalRepository.findByFarmerUserEmail(farmerEmail);
    }

    public List<Withdrawal> getAllWithdrawals() {
        return withdrawalRepository.findAll();
    }
}
