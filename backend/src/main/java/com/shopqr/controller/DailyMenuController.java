package com.shopqr.controller;

import com.shopqr.model.DailyMenu;
import com.shopqr.repository.DailyMenuRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/menu")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:8081")
public class DailyMenuController {
    private final DailyMenuRepository dailyMenuRepository;

    @GetMapping("/today")
    public ResponseEntity<?> getTodayMenu() {
        return dailyMenuRepository.findByMenuDate(LocalDate.now())
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.ok().body((DailyMenu) null));
    }

    @PostMapping("/update")
    public ResponseEntity<?> updateMenu(@RequestBody Map<String, String> request) {
        String content = request.get("content");
        LocalDate date = LocalDate.now();
        
        DailyMenu menu = dailyMenuRepository.findByMenuDate(date)
                .orElse(new DailyMenu());
        
        menu.setMenuDate(date);
        menu.setContent(content);
        dailyMenuRepository.save(menu);
        
        return ResponseEntity.ok(Map.of("message", "Menu updated for " + date));
    }
}
