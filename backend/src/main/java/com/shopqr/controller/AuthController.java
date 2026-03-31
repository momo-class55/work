package com.shopqr.controller;

import com.shopqr.model.Company;
import com.shopqr.model.User;
import com.shopqr.repository.CompanyRepository;
import com.shopqr.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:8081")
public class AuthController {
    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, String> request) {
        String phoneNumber = request.get("phoneNumber");
        String name = request.get("name");
        String password = request.get("password");
        String passwordConfirm = request.get("passwordConfirm");
        String companyName = request.get("companyName");

        if (userRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Phone number already exists"));
        }
        if (!password.equals(passwordConfirm)) {
            return ResponseEntity.badRequest().body(Map.of("error", "Passwords do not match"));
        }

        Company company = companyRepository.findByName(companyName)
                .orElseGet(() -> {
                    Company newCompany = new Company();
                    newCompany.setName(companyName);
                    newCompany.setSettlementCycle("MONTHLY");
                    return companyRepository.save(newCompany);
                });

        User user = new User();
        user.setPhoneNumber(phoneNumber);
        user.setName(name);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(User.Role.USER);
        user.setCompany(company);
        user.setApproved(false);

        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "User registered. Waiting for admin approval."));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> request) {
        String phoneNumber = request.get("phoneNumber");
        String password = request.get("password");

        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElse(null);

        if (user == null || !passwordEncoder.matches(password, user.getPassword())) {
            return ResponseEntity.status(401).body(Map.of("error", "Invalid phone number or password"));
        }

        if (!user.isApproved()) {
            return ResponseEntity.status(403).body(Map.of("error", "Your account is pending approval."));
        }

        return ResponseEntity.ok(Map.of(
            "message", "Login successful",
            "userId", user.getId(),
            "name", user.getName(),
            "role", user.getRole()
        ));
    }
}
