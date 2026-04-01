package com.shopqr.repository;

import com.shopqr.model.MealLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

public interface MealLogRepository extends JpaRepository<MealLog, Long> {
    @Query("SELECT m FROM MealLog m WHERE m.user.id = :userId AND m.timestamp >= :startTime ORDER BY m.timestamp DESC")
    List<MealLog> findRecentLogs(@Param("userId") Long userId, @Param("startTime") LocalDateTime startTime);
    
    List<MealLog> findByUserIdAndTimestampAfter(Long userId, LocalDateTime timestamp);
}
