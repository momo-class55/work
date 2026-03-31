package com.shopqr.controller;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.shopqr.service.MealService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayOutputStream;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:8081")
public class MealController {
    private final MealService mealService;

    // 1. QR generation for user (accepts ID or phone number)
    @GetMapping({"/api/meal/qr-token/{identifier}", "/qr-token/{identifier}"})
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

    // 2. Scan QR from tablet device
    @PostMapping({"/api/meal/scan", "/scan"})
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

    // 3. Return QR Image directly
    @GetMapping(value = {"/api/meal/qr-image/{userId}", "/qr-image/{userId}"}, produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getQrImage(@PathVariable Long userId) {
        try {
            String token = mealService.generateQrToken(userId);
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(token, BarcodeFormat.QR_CODE, 300, 300);

            ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
            byte[] bytes = pngOutputStream.toByteArray();

            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_PNG)
                    .body(bytes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
