package com.althaea.dto;

import com.althaea.model.WorkoutLog;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
public record WorkoutLogRequest(
        @NotNull LocalDate date,
        @NotNull WorkoutLog.WorkoutType type,
        int durationMinutes,
        int estimatedCaloriesBurned,
        String notes
) {}
