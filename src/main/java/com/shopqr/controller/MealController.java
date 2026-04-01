package com.shopqr.controller;

import com.shopqr.service.MealService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 태블릿·키오스크 등 외부 기기용 JSON API (웹 UI와 무관)
 */
@RestController
@RequiredArgsConstructor
@Tag(name = "Meal / QR", description = "QR 토큰 발급 및 식사 스캔")
public class MealController {

    private final MealService mealService;

    @GetMapping({"/api/meal/qr-token/{identifier}", "/qr-token/{identifier}"})
    @Operation(summary = "QR 토큰 조회", description = "전화번호(0으로 시작) 또는 사용자 ID")
    public ResponseEntity<?> getQrToken(@PathVariable String identifier) {
        try {
            String token;
            if (identifier.startsWith("0")) {
                token = mealService.generateQrTokenByPhone(identifier);
            } else {
                try {
                    token = mealService.generateQrToken(Long.valueOf(identifier));
                } catch (NumberFormatException e) {
                    token = mealService.generateQrTokenByPhone(identifier);
                }
            }
            return ResponseEntity.ok(Map.of("qrToken", token));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PostMapping({"/api/meal/scan", "/scan"})
    @Operation(summary = "QR 스캔(식사 확인)", description = "body: qrToken, 선택 locationId")
    public ResponseEntity<?> scanQr(@RequestBody Map<String, String> request) {
        String token = request.get("qrToken");
        String locationId = request.getOrDefault("locationId", "tablet_main");

        try {
            mealService.scanQr(token, locationId);
            return ResponseEntity.ok(Map.of("message", "Meal verification successful!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
