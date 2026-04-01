package com.shopqr.controller;

import com.shopqr.model.Company;
import com.shopqr.model.User;
import com.shopqr.repository.CompanyRepository;
import com.shopqr.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class AuthPagesController {

    private final UserRepository userRepository;
    private final CompanyRepository companyRepository;
    private final PasswordEncoder passwordEncoder;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/signup")
    public String signupForm() {
        return "signup";
    }

    @PostMapping("/signup")
    public String signup(
            @RequestParam String phoneNumber,
            @RequestParam String name,
            @RequestParam String password,
            @RequestParam String passwordConfirm,
            @RequestParam String companyName,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (userRepository.findByPhoneNumber(phoneNumber).isPresent()) {
            model.addAttribute("error", "이미 등록된 전화번호입니다.");
            return "signup";
        }
        if (!password.equals(passwordConfirm)) {
            model.addAttribute("error", "비밀번호가 일치하지 않습니다.");
            return "signup";
        }

        Company company = companyRepository.findByName(companyName)
                .orElseGet(() -> {
                    Company c = new Company();
                    c.setName(companyName);
                    c.setSettlementCycle("MONTHLY");
                    return companyRepository.save(c);
                });

        User user = new User();
        user.setPhoneNumber(phoneNumber);
        user.setName(name);
        user.setPassword(passwordEncoder.encode(password));
        user.setRole(User.Role.USER);
        user.setCompany(company);
        user.setApproved(false);
        userRepository.save(user);

        redirectAttributes.addFlashAttribute("registered", true);
        return "redirect:/login";
    }
}
