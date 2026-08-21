package com.althaea.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

/** Workout session logged by user */
@Entity
@Table(name = "workout_logs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class WorkoutLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private LocalDate workoutDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private WorkoutType type;

    /** Duration in minutes */
    private int durationMinutes;

    /** Estimated calories burned */
    private int estimatedCaloriesBurned;

    private String notes;

    public enum WorkoutType {
        STRENGTH,       // weight training — prioritise protein
        HIIT,           // high intensity — prioritise carbs
        CARDIO,         // steady state — moderate carbs
        YOGA_MOBILITY,  // recovery — lower cal day
        REST            // rest day — maintenance or slight deficit
    }
}
