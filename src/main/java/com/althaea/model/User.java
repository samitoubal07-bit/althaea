package com.althaea.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.List;

/** Represents an Althaea user and their information */
@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    @Email
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
    private String name;

    // --- Fitness profile ---
    private int age;
    private double weightKg;
    private double heightCm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Sex sex;

    /** CUT / MAINTAIN / BULK */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BodyGoal bodyGoal;

    /** FAT_LOSS / ENDURANCE / MUSCLE_GAIN */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FitnessGoal fitnessGoal;

    @Enumerated(EnumType.STRING)
    private ActivityLevel activityLevel;

    // --- Relationships ---
    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FridgeItem> fridgeItems;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WorkoutLog> workoutLogs;

    @JsonIgnore
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<MealPlan> mealPlans;

    // --- Enums ---
    public enum Sex            { MALE, FEMALE }
    public enum BodyGoal       { CUT, MAINTAIN, BULK }
    public enum FitnessGoal    { FAT_LOSS, ENDURANCE, MUSCLE_GAIN }
    public enum ActivityLevel  { SEDENTARY, LIGHTLY_ACTIVE, MODERATELY_ACTIVE, ACTIVE, EXTREMELY_ACTIVE }
}
