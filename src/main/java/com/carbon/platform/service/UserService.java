package com.carbon.platform.service;

import com.carbon.platform.config.JwtService;
import com.carbon.platform.dto.request.*;
import com.carbon.platform.dto.response.LoginResponse;
import com.carbon.platform.entity.Company;
import com.carbon.platform.entity.Farmer;
import com.carbon.platform.entity.FarmerWallet;
import com.carbon.platform.entity.User;
import com.carbon.platform.enums.ApprovalStatus;
import com.carbon.platform.enums.Role;
import com.carbon.platform.exception.DuplicateRegistrationException;
import com.carbon.platform.exception.EntityNotFoundException;
import com.carbon.platform.exception.InvalidActionException;
import com.carbon.platform.exception.OtpExpiredException;
import com.carbon.platform.repository.CompanyRepository;
import com.carbon.platform.repository.FarmerRepository;
import com.carbon.platform.repository.FarmerWalletRepository;
import com.carbon.platform.repository.UserRepository;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final FarmerRepository farmerRepository;
    private final CompanyRepository companyRepository;
    private final FarmerWalletRepository walletRepository;
    private final OtpService otpService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    public UserService(UserRepository userRepository,
                       FarmerRepository farmerRepository,
                       CompanyRepository companyRepository,
                       FarmerWalletRepository walletRepository,
                       OtpService otpService,
                       JwtService jwtService,
                       PasswordEncoder passwordEncoder,
                       ApplicationEventPublisher eventPublisher) {
        this.userRepository = userRepository;
        this.farmerRepository = farmerRepository;
        this.companyRepository = companyRepository;
        this.walletRepository = walletRepository;
        this.otpService = otpService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public String registerFarmer(FarmerRegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateRegistrationException("Email is already registered");
        }
        if (userRepository.existsByMobile(request.getMobile())) {
            throw new DuplicateRegistrationException("Mobile number is already registered");
        }
        if (farmerRepository.existsByAadhaarNumber(request.getAadhaarNumber())) {
            throw new DuplicateRegistrationException("Aadhaar number is already registered");
        }

        User user = new User(
                request.getEmail(),
                request.getMobile(),
                passwordEncoder.encode(request.getPassword()),
                request.getName(),
                request.getAddress(),
                Role.FARMER,
                ApprovalStatus.PENDING
        );
        User savedUser = userRepository.save(user);

        Farmer farmer = new Farmer(savedUser, request.getAadhaarNumber(), ApprovalStatus.PENDING);
        Farmer savedFarmer = farmerRepository.save(farmer);

        // Auto-initialize Wallet
        FarmerWallet wallet = new FarmerWallet(savedFarmer);
        walletRepository.save(wallet);

        // Generate Registration OTP (mobile verification)
        return otpService.generateOtp(request.getMobile());
    }

    @Transactional
    public String registerCompany(CompanyRegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateRegistrationException("Email is already registered");
        }
        if (userRepository.existsByMobile(request.getMobile())) {
            throw new DuplicateRegistrationException("Mobile number is already registered");
        }
        if (companyRepository.existsByGstNumber(request.getGstNumber())) {
            throw new DuplicateRegistrationException("GST number is already registered");
        }
        if (companyRepository.existsByPanNumber(request.getPanNumber())) {
            throw new DuplicateRegistrationException("PAN number is already registered");
        }

        User user = new User(
                request.getEmail(),
                request.getMobile(),
                passwordEncoder.encode(request.getPassword()),
                request.getName(),
                request.getAddress(),
                Role.COMPANY,
                ApprovalStatus.PENDING
        );
        User savedUser = userRepository.save(user);

        Company company = new Company(
                savedUser,
                request.getCompanyName(),
                request.getGstNumber(),
                request.getPanNumber(),
                ApprovalStatus.PENDING
        );
        companyRepository.save(company);

        // Generate Registration OTP (email verification)
        return otpService.generateOtp(request.getEmail());
    }

    @Transactional
    public boolean verifyRegistrationOtp(OtpVerifyRequest request) {
        boolean verified = otpService.verifyOtp(request.getTarget(), request.getCode());
        if (!verified) {
            throw new OtpExpiredException("Invalid or expired OTP");
        }

        User user = userRepository.findByEmail(request.getTarget())
                .orElseGet(() -> userRepository.findByMobile(request.getTarget()).orElse(null));

        if (user != null) {
            if (user.getRole() == Role.FARMER) {
                user.setMobileVerified(true);
            } else if (user.getRole() == Role.COMPANY) {
                user.setEmailVerified(true);
            }
            userRepository.save(user);
            return true;
        }
        return false;
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getUsername())
                .orElseGet(() -> userRepository.findByMobile(request.getUsername())
                        .orElseThrow(() -> new EntityNotFoundException("Invalid email/mobile or password")));

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            throw new EntityNotFoundException("Invalid email/mobile or password");
        }

        if (user.getStatus() == ApprovalStatus.SUSPENDED) {
            throw new InvalidActionException("Your account is suspended. Please contact support.");
        }

        // Generate spring security user details
        org.springframework.security.core.userdetails.UserDetails userDetails =
                new org.springframework.security.core.userdetails.User(
                        user.getEmail(),
                        user.getPassword(),
                        user.getStatus() != ApprovalStatus.SUSPENDED,
                        true, true, true,
                        java.util.Collections.singletonList(
                                new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + user.getRole().name())
                        )
                );

        String token = jwtService.generateToken(userDetails);

        return new LoginResponse(
                token,
                user.getEmail(),
                user.getRole().name(),
                user.getName(),
                user.getStatus().name()
        );
    }

    public String initiateForgotPassword(ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getTarget())
                .orElseGet(() -> userRepository.findByMobile(request.getTarget())
                        .orElseThrow(() -> new EntityNotFoundException("No account registered with this email or mobile")));

        return otpService.generateOtp(request.getTarget());
    }

    @Transactional
    public void resetPassword(ResetPasswordRequest request) {
        boolean verified = otpService.verifyOtp(request.getTarget(), request.getCode());
        if (!verified) {
            throw new OtpExpiredException("Invalid or expired OTP");
        }

        User user = userRepository.findByEmail(request.getTarget())
                .orElseGet(() -> userRepository.findByMobile(request.getTarget())
                        .orElseThrow(() -> new EntityNotFoundException("User not found")));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
    }

    @Transactional
    public User updateProfile(String email, String name, String address) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
        user.setName(name);
        user.setAddress(address);
        return userRepository.save(user);
    }
}
