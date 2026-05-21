package com.carbon.platform.config;

import com.carbon.platform.entity.CreditFormula;
import com.carbon.platform.entity.User;
import com.carbon.platform.enums.ApprovalStatus;
import com.carbon.platform.enums.PracticeType;
import com.carbon.platform.enums.Role;
import com.carbon.platform.repository.CreditFormulaRepository;
import com.carbon.platform.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final CreditFormulaRepository creditFormulaRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository,
                           CreditFormulaRepository creditFormulaRepository,
                           PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.creditFormulaRepository = creditFormulaRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // 1. Seed supervisor user if not present
        if (userRepository.findByEmail("supervisor@carbon.com").isEmpty()) {
            User supervisor = new User();
            supervisor.setEmail("supervisor@carbon.com");
            supervisor.setMobile("9999999999");
            supervisor.setPassword(passwordEncoder.encode("Supervisor@123"));
            supervisor.setName("Platform Supervisor");
            supervisor.setAddress("Platform Head Office");
            supervisor.setRole(Role.SUPERVISOR);
            supervisor.setStatus(ApprovalStatus.APPROVED);
            supervisor.setEmailVerified(true);
            supervisor.setMobileVerified(true);
            userRepository.save(supervisor);
            System.out.println("Seeded Default Supervisor User: supervisor@carbon.com / Supervisor@123");
        }

        // 2. Seed admin user if not present
        if (userRepository.findByEmail("admin@carbon.com").isEmpty()) {
            User admin = new User();
            admin.setEmail("admin@carbon.com");
            admin.setMobile("8888888888");
            admin.setPassword(passwordEncoder.encode("Admin@123"));
            admin.setName("Platform Admin");
            admin.setAddress("Platform Head Office");
            admin.setRole(Role.ADMIN);
            admin.setStatus(ApprovalStatus.APPROVED);
            admin.setEmailVerified(true);
            admin.setMobileVerified(true);
            userRepository.save(admin);
            System.out.println("Seeded Default Admin User: admin@carbon.com / Admin@123");
        }

        // 3. Seed Credit Formulas for each PracticeType if not present
        for (PracticeType type : PracticeType.values()) {
            if (creditFormulaRepository.findByPracticeType(type).isEmpty()) {
                CreditFormula formula = new CreditFormula();
                formula.setPracticeType(type);
                formula.setVersion("1.0");
                formula.setActive(true);

                // Set coefficients and multipliers depending on the practice type
                switch (type) {
                    case TREE_PLANTATION -> {
                        formula.setBaseCoefficient(2.5);
                        formula.setKharifMultiplier(1.2);
                        formula.setRabiMultiplier(1.1);
                        formula.setZaidMultiplier(1.0);
                        formula.setFoodCropMultiplier(1.0);
                        formula.setCashCropMultiplier(1.1);
                        formula.setPlantationCropMultiplier(1.3);
                        formula.setHorticultureCropMultiplier(1.2);
                        formula.setMaxCap(500.0);
                    }
                    case ORGANIC_FARMING -> {
                        formula.setBaseCoefficient(1.8);
                        formula.setKharifMultiplier(1.1);
                        formula.setRabiMultiplier(1.2);
                        formula.setZaidMultiplier(1.0);
                        formula.setFoodCropMultiplier(1.3);
                        formula.setCashCropMultiplier(1.1);
                        formula.setPlantationCropMultiplier(1.0);
                        formula.setHorticultureCropMultiplier(1.2);
                        formula.setMaxCap(400.0);
                    }
                    case SOLAR -> {
                        formula.setBaseCoefficient(3.0);
                        formula.setKharifMultiplier(1.0);
                        formula.setRabiMultiplier(1.0);
                        formula.setZaidMultiplier(1.2);
                        formula.setFoodCropMultiplier(1.0);
                        formula.setCashCropMultiplier(1.0);
                        formula.setPlantationCropMultiplier(1.0);
                        formula.setHorticultureCropMultiplier(1.0);
                        formula.setMaxCap(800.0);
                    }
                    case WATER_CONSERVATION -> {
                        formula.setBaseCoefficient(1.5);
                        formula.setKharifMultiplier(1.3);
                        formula.setRabiMultiplier(1.0);
                        formula.setZaidMultiplier(0.9);
                        formula.setFoodCropMultiplier(1.1);
                        formula.setCashCropMultiplier(1.1);
                        formula.setPlantationCropMultiplier(1.2);
                        formula.setHorticultureCropMultiplier(1.2);
                        formula.setMaxCap(350.0);
                    }
                    case SOIL_MANAGEMENT -> {
                        formula.setBaseCoefficient(2.0);
                        formula.setKharifMultiplier(1.1);
                        formula.setRabiMultiplier(1.1);
                        formula.setZaidMultiplier(1.0);
                        formula.setFoodCropMultiplier(1.2);
                        formula.setCashCropMultiplier(1.2);
                        formula.setPlantationCropMultiplier(1.0);
                        formula.setHorticultureCropMultiplier(1.1);
                        formula.setMaxCap(450.0);
                    }
                }
                creditFormulaRepository.save(formula);
                System.out.println("Seeded Default Credit Formula for " + type + " (v1.0)");
            }
        }
    }
}
