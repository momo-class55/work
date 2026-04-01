package com.shopqr.controller;

import com.shopqr.model.DailyMenu;
import com.shopqr.repository.DailyMenuRepository;
import com.shopqr.security.ShopUserPrincipal;
import com.shopqr.service.MealService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class UserPageController {

    private final DailyMenuRepository dailyMenuRepository;
    private final MealService mealService;

    @GetMapping("/user/home")
    public String home(@AuthenticationPrincipal ShopUserPrincipal principal, Model model) {
        model.addAttribute("user", principal.getUser());
        DailyMenu menu = dailyMenuRepository.findByMenuDate(LocalDate.now()).orElse(null);
        model.addAttribute("menu", menu);
        return "user/home";
    }

    @GetMapping(value = "/user/qr.png", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> qrPng(@AuthenticationPrincipal ShopUserPrincipal principal) throws Exception {
        byte[] png = mealService.buildQrPng(principal.getUser().getId());
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(png);
    }
}
