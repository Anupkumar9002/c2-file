package com.carbon.platform.service;

import com.carbon.platform.entity.OtpEntity;
import com.carbon.platform.repository.OtpRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
public class OtpService {

    private final OtpRepository otpRepository;
    private final Random random = new Random();

    @Value("${app.otp.expiry.minutes:10}")
    private int otpExpiryMinutes;

    public OtpService(OtpRepository otpRepository) {
        this.otpRepository = otpRepository;
    }

    public String generateOtp(String target) {
        // Generate a 6-digit OTP code
        String code = String.format("%06d", random.nextInt(1000000));
        LocalDateTime expiryTime = LocalDateTime.now().plusMinutes(otpExpiryMinutes);
        OtpEntity otpEntity = new OtpEntity(target, code, expiryTime);
        otpRepository.save(otpEntity);

        // Standard requirement: log to console in dev/H2 env
        System.out.println("==================================================");
        System.out.println("   OTP GENERATED FOR: " + target);
        System.out.println("   CODE: " + code);
        System.out.println("   EXPIRY: " + expiryTime);
        System.out.println("==================================================");

        return code;
    }

    public boolean verifyOtp(String target, String code) {
        Optional<OtpEntity> opt = otpRepository.findTopByTargetAndCodeAndUsedFalseOrderByExpiryDesc(target, code);
        if (opt.isEmpty()) {
            return false;
        }

        OtpEntity otp = opt.get();
        if (otp.getExpiry().isBefore(LocalDateTime.now())) {
            return false;
        }

        otp.setUsed(true);
        otpRepository.save(otp);
        return true;
    }
}
