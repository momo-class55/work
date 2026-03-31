package com.shopqr.config;

import com.shopqr.model.Company;
import com.shopqr.model.User;
import com.shopqr.repository.CompanyRepository;
import com.shopqr.repository.DailyMenuRepository;
import com.shopqr.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final DailyMenuRepository dailyMenuRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.count() == 0) {
            Company company = new Company();
            company.setName("Google DeepMind");
            company.setSettlementCycle("MONTHLY");
            companyRepository.save(company);

            User user = new User();
            user.setPhoneNumber("01012345678");
            user.setName("Test User");
            user.setPassword(passwordEncoder.encode("password"));
            user.setRole(User.Role.USER);
            user.setCompany(company);
            user.setApproved(true); 
            userRepository.save(user);

            User admin = new User();
            admin.setPhoneNumber("01000000000");
            admin.setName("Admin");
            admin.setPassword(passwordEncoder.encode("password"));
            admin.setRole(User.Role.ADMIN);
            admin.setCompany(company);
            admin.setApproved(true);
            userRepository.save(admin);

            System.out.println("Sample user created: 01012345678 / password");
            System.out.println("Sample admin created: 01000000000 / password");
        }

        if (dailyMenuRepository.findByMenuDate(java.time.LocalDate.now()).isEmpty()) {
            com.shopqr.model.DailyMenu menu = new com.shopqr.model.DailyMenu();
            menu.setMenuDate(java.time.LocalDate.now());
            menu.setContent("불고기 비빔밥, 북어국, 깍두기, 시금치 나물, 식혜");
            dailyMenuRepository.save(menu);
            System.out.println("Today's menu created.");
        }
    }
}
