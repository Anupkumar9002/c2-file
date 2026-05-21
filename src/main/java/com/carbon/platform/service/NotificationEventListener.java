package com.carbon.platform.service;

import com.carbon.platform.entity.InAppNotification;
import com.carbon.platform.event.*;
import com.carbon.platform.repository.InAppNotificationRepository;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class NotificationEventListener {

    private final InAppNotificationRepository notificationRepository;

    public NotificationEventListener(InAppNotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Async
    @EventListener
    public void handleFarmerApproval(FarmerApprovalEvent event) {
        String msg = "Your farmer onboarding request has been " + event.getStatus() + ". Comments: " + event.getComments();
        notificationRepository.save(new InAppNotification(event.getUserId(), msg));
        System.out.println("[NOTIFICATION][SMS/EMAIL] sent to " + event.getEmail() + ": " + msg);
    }

    @Async
    @EventListener
    public void handlePracticeApproval(PracticeApprovalEvent event) {
        String msg = "Your practice log for parcel " + event.getParcelName() + " has been " + event.getStatus() + ". Comments: " + event.getComments();
        notificationRepository.save(new InAppNotification(event.getUserId(), msg));
        System.out.println("[NOTIFICATION][SMS/EMAIL] sent to farmer: " + msg);
    }

    @Async
    @EventListener
    public void handleCreditVerification(CreditVerificationEvent event) {
        String msg = "Your carbon credits of quantity " + event.getQuantity() + " have been verified.";
        notificationRepository.save(new InAppNotification(event.getUserId(), msg));
        System.out.println("[NOTIFICATION][SMS/EMAIL] sent to farmer: " + msg);
    }

    @Async
    @EventListener
    public void handleCreditSale(CreditSaleEvent event) {
        // notify farmer
        String msgFarmer = "You sold " + event.getQuantity() + " carbon credits for a net amount of INR " + event.getNetAmount();
        notificationRepository.save(new InAppNotification(event.getFarmerUserId(), msgFarmer));
        System.out.println("[NOTIFICATION][SMS/EMAIL] sent to farmer: " + msgFarmer);

        // notify company
        String msgCompany = "You successfully purchased " + event.getQuantity() + " carbon credits for total INR " + event.getTotalAmount();
        notificationRepository.save(new InAppNotification(event.getCompanyUserId(), msgCompany));
        System.out.println("[NOTIFICATION][SMS/EMAIL] sent to company: " + msgCompany);
    }

    @Async
    @EventListener
    public void handleWalletCredit(WalletCreditEvent event) {
        String msg = "Your wallet has been credited with INR " + event.getAmount() + ". Current Balance: INR " + event.getBalance();
        notificationRepository.save(new InAppNotification(event.getUserId(), msg));
        System.out.println("[NOTIFICATION][SMS/EMAIL] sent to farmer: " + msg);
    }

    @Async
    @EventListener
    public void handleWithdrawalStatus(WithdrawalStatusEvent event) {
        String msg = "Your withdrawal request of INR " + event.getAmount() + " is now " + event.getStatus() + ". Comments: " + event.getComments();
        notificationRepository.save(new InAppNotification(event.getUserId(), msg));
        System.out.println("[NOTIFICATION][SMS/EMAIL] sent to farmer: " + msg);
    }

    @Async
    @EventListener
    public void handleCertificateIssued(CertificateIssuedEvent event) {
        String msg = "Your carbon offset certificate " + event.getCertificateId() + " has been issued for " + event.getOffsetValue() + " tonnes of CO2.";
        notificationRepository.save(new InAppNotification(event.getUserId(), msg));
        System.out.println("[NOTIFICATION][SMS/EMAIL] sent to company: " + msg);
    }
}
