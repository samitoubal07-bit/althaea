package com.althaea.controller;
import com.althaea.dto.WorkoutLogRequest;
import com.althaea.model.User;
import com.althaea.model.WorkoutLog;
import com.althaea.repository.WorkoutLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
@RestController
@RequestMapping("/api/workouts")
@RequiredArgsConstructor
public class WorkoutController {
    private final WorkoutLogRepository workoutRepo;
    @GetMapping
    public ResponseEntity<List<WorkoutLog>> getLogs(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(workoutRepo.findByUserOrderByWorkoutDateDesc(user));
    }
    @PostMapping
    public ResponseEntity<WorkoutLog> logWorkout(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody WorkoutLogRequest req) {
        WorkoutLog log = WorkoutLog.builder()
                .user(user)
                .workoutDate(req.date())
                .type(req.type())
                .durationMinutes(req.durationMinutes())
                .estimatedCaloriesBurned(req.estimatedCaloriesBurned())
                .notes(req.notes())
                .build();
        return ResponseEntity.ok(workoutRepo.save(log));
    }
}
