package com.shopqr.controller;

import com.shopqr.model.MealLog;
import com.shopqr.model.User;
import com.shopqr.repository.MealLogRepository;
import com.shopqr.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:8081")
public class AdminController {
    private final UserRepository userRepository;
    private final MealLogRepository mealLogRepository;

    // 1. List all users for approval
    @GetMapping("/users/unapproved")
    public List<User> getUnapprovedUsers() {
        return userRepository.findByIsApproved(false);
    }

    // 2. Approve user
    @PostMapping("/users/{userId}/approve")
    public ResponseEntity<?> approveUser(@PathVariable Long userId) {
        User user = userRepository.findById(userId).orElseThrow();
        user.setApproved(true);
        userRepository.save(user);
        return ResponseEntity.ok(Map.of("message", "User approved successfully"));
    }

    // 3. Simple daily stats (can be extended for monthly)
    @GetMapping("/stats/daily")
    public Map<LocalDate, Long> getDailyStats() {
        List<MealLog> allLogs = mealLogRepository.findAll();
        return allLogs.stream()
                .collect(Collectors.groupingBy(
                        log -> log.getTimestamp().toLocalDate(),
                        Collectors.counting()
                ));
    }
}
