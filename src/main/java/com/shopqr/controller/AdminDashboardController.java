package com.shopqr.controller;

import com.shopqr.model.DailyMenu;
import com.shopqr.model.MealLog;
import com.shopqr.model.User;
import com.shopqr.repository.DailyMenuRepository;
import com.shopqr.repository.MealLogRepository;
import com.shopqr.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequiredArgsConstructor
public class AdminDashboardController {

    private final UserRepository userRepository;
    private final MealLogRepository mealLogRepository;
    private final DailyMenuRepository dailyMenuRepository;

    @GetMapping("/admin/dashboard")
    public String dashboard(Model model) {
        List<User> unapproved = userRepository.findByIsApproved(false);
        model.addAttribute("unapproved", unapproved);

        List<MealLog> allLogs = mealLogRepository.findAll();
        Map<LocalDate, Long> stats = allLogs.stream()
                .collect(Collectors.groupingBy(
                        log -> log.getTimestamp().toLocalDate(),
                        Collectors.counting()));
        model.addAttribute("stats", stats);

        DailyMenu today = dailyMenuRepository.findByMenuDate(LocalDate.now()).orElse(null);
        model.addAttribute("todayMenu", today);
        return "admin/dashboard";
    }

    @PostMapping("/admin/users/{userId}/approve")
    public String approve(@PathVariable Long userId, RedirectAttributes ra) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setApproved(true);
        userRepository.save(user);
        ra.addFlashAttribute("approved", true);
        return "redirect:/admin/dashboard";
    }

    @PostMapping("/admin/menu/today")
    public String updateTodayMenu(@RequestParam String content, RedirectAttributes ra) {
        LocalDate date = LocalDate.now();
        DailyMenu menu = dailyMenuRepository.findByMenuDate(date).orElse(new DailyMenu());
        menu.setMenuDate(date);
        menu.setContent(content);
        dailyMenuRepository.save(menu);
        ra.addFlashAttribute("menuSaved", true);
        return "redirect:/admin/dashboard";
    }
}
