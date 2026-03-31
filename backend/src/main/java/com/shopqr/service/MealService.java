package com.shopqr.service;

import com.shopqr.model.MealLog;
import com.shopqr.model.User;
import com.shopqr.repository.MealLogRepository;
import com.shopqr.repository.UserRepository;
import com.shopqr.util.AES256Util;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MealService {
    private final AES256Util aes256Util;
    private final MealLogRepository mealLogRepository;
    private final UserRepository userRepository;

    public String generateQrToken(Long userId) throws Exception {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));
        return generateTokenForUser(user);
    }

    public String generateQrTokenByPhone(String phoneNumber) throws Exception {
        User user = userRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new IllegalArgumentException("User with phone number not found"));
        return generateTokenForUser(user);
    }

    private String generateTokenForUser(User user) throws Exception {
        if (!user.isApproved()) {
            throw new IllegalStateException("User not approved yet.");
        }

        long timestamp = System.currentTimeMillis();
        String plainText = user.getId() + ":" + timestamp;
        return aes256Util.encrypt(plainText);
    }

    @Transactional
    public void scanQr(String encryptedToken, String locationId) throws Exception {
        String decrypted = aes256Util.decrypt(encryptedToken);
        String[] parts = decrypted.split(":");
        if (parts.length != 2) throw new IllegalArgumentException("Invalid QR format");

        Long userId = Long.valueOf(parts[0]);
        long timestamp = Long.parseLong(parts[1]);

        // 1. Time validity check (within 1 minute)
        if (System.currentTimeMillis() - timestamp > 60000) {
            throw new IllegalStateException("QR code expired.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found."));

        if (!user.isApproved()) {
            throw new IllegalStateException("Access denied: Not approved.");
        }

        // 2. Duplicate check (within last 1 hour)
        LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
        List<MealLog> recentLogs = mealLogRepository.findByUserIdAndTimestampAfter(userId, oneHourAgo);
        if (!recentLogs.isEmpty()) {
            throw new IllegalStateException("Duplicate meal check-in detected. Please wait.");
        }

        // 3. Log meal
        MealLog mealLog = new MealLog();
        mealLog.setUser(user);
        mealLog.setTimestamp(LocalDateTime.now());
        mealLog.setCheckLocation(locationId);
        mealLogRepository.save(mealLog);
    }
}
